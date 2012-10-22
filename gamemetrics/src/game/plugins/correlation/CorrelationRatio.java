package game.plugins.correlation;

import game.core.Dataset.SampleIterator;
import game.core.Sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics;

import Jama.Matrix;

public class CorrelationRatio extends CorrelationMeasure {
	
	public boolean unbiased = true;

	@Override
	public void computeInputCorrelationMatrix(SampleIterator it, int samples) {
		// FIXME Some correlation coefficients cannot be used for input correlation
	}

	@Override
	public void computeIOCorrelationMatrix(SampleIterator it, int samples) {
		it.reset();
		Sample sample = it.next();
		int inputDim = sample.getEncodedInput().getDimension();
		int outputDim = sample.getEncodedOutput().getDimension();
		
		ioCorrelationMatrix = new Array2DRowRealMatrix(inputDim, outputDim);
		
		for(int i = 0; i < inputDim; i++) {
			for(int j = 0; j < outputDim; j++) {
				it.reset();
				ioCorrelationMatrix.setEntry(i, j, correlationRatio(it, i, j, samples));
			}
		}
	}

	private double correlationRatio(SampleIterator it, int in, int out, int samples) {
		Map<Double, Integer> n_y = new HashMap<>();
		Map<Double, RealVector> x_y = new HashMap<>();
		double[] x = new double[samples];
		
		for(int i = 0; i < samples; i++) {
			Sample sample = it.next();
			double input = sample.getEncodedInput().getEntry(in);
			double output = sample.getEncodedOutput().getEntry(out);
			if (!n_y.containsKey(output)) {
				n_y.put(output, 0);
				x_y.put(output, new ArrayRealVector());
			}
			
			n_y.put(output, n_y.get(output)+1);
			x_y.put(output, x_y.get(output).append(input));
			x[i] = input;
		}
		
		double x_mean = StatUtils.mean(x);
		Map<Double, Double> x_y_mean = new HashMap<>();
		for(Entry<Double, RealVector> entry: x_y.entrySet()) {
			x_y_mean.put(entry.getKey(), StatUtils.mean(entry.getValue().toArray()));
		}
		
		double numerator = 0;
		for(double key: n_y.keySet()) {
			numerator += n_y.get(key) * (x_y_mean.get(key) - x_mean) * (x_y_mean.get(key) - x_mean);
		}
		double denominator = 0;
		for(double e: x) {
			denominator += (e - x_mean) * (e - x_mean);
		}
		
		double eta = denominator == 0 ? 0 : Math.sqrt(numerator / denominator);
		
		return eta;
	}

	@Override
	public void computeSyntheticValues(SampleIterator it, int samples) {
		it.reset();
		Sample sample = it.next();
		int inputDim = sample.getEncodedInput().getDimension();
		int outputDim = sample.getEncodedOutput().getDimension();
		
		syntheticValues = new ArrayRealVector(outputDim);
		for(int out = 0; out < outputDim; out++) {
			it.reset();
			syntheticValues.setEntry(out, generalizedCorrelationRatio(it, inputDim, out, samples));
		}
	}
	
	private double generalizedCorrelationRatio(SampleIterator it, int inputDim, int out, int samples) {
		Map<Double, Integer> n_y = new HashMap<>();
		Map<Double, MultivariateSummaryStatistics> stat_y = new HashMap<>();
		RealMatrix[] x = new RealMatrix[samples];
		MultivariateSummaryStatistics stat = new MultivariateSummaryStatistics(inputDim, unbiased);
		
		for(int i = 0; i < samples; i++) {
			Sample sample = it.next();
			double[] input = sample.getEncodedInput().toArray();
			double output = sample.getEncodedOutput().getEntry(out);
			if (!n_y.containsKey(output)) {
				n_y.put(output, 0);
				stat_y.put(output, new MultivariateSummaryStatistics(inputDim, unbiased));
			}
			
			n_y.put(output, n_y.get(output)+1);
			stat_y.get(output).addValue(input);
			x[i] = new Array2DRowRealMatrix(input);
			stat.addValue(input);
		}
		
		RealMatrix x_sum = new Array2DRowRealMatrix(stat.getSum());
		Map<Double, RealMatrix> x_y_sum = new HashMap<>();
		for(Entry<Double, MultivariateSummaryStatistics> entry: stat_y.entrySet()) {
			x_y_sum.put(entry.getKey(), new Array2DRowRealMatrix(entry.getValue().getSum()));
		}
		
		RealMatrix H = new Array2DRowRealMatrix(inputDim, inputDim);
		RealMatrix temp = new Array2DRowRealMatrix(inputDim, inputDim);
		
		for(double key: n_y.keySet()) {
			temp = temp.add(x_y_sum.get(key).multiply(x_y_sum.get(key).transpose()).scalarMultiply(1.0 / n_y.get(key)));
		}
		H = temp.subtract(x_sum.multiply(x_sum.transpose()).scalarMultiply(1.0 / samples));
		
		RealMatrix E = new Array2DRowRealMatrix(inputDim, inputDim);
		for(RealMatrix m: x) {
			E = E.add(m.multiply(m.transpose()));
		}
		E = E.subtract(temp);
		
		List<Integer> zeroColumns = findZeroColumns(E);
		E = removeZeroColumns(E, zeroColumns);
		H = removeZeroColumns(H, zeroColumns);
		
		RealMatrix Einverse = new LUDecomposition(E).getSolver().getInverse();
		
		Matrix L = new Matrix(Einverse.multiply(H).getData());
		double[] eigs = L.eig().getRealEigenvalues();
		
		double lambda = 1;
		int nonNullEigs = n_y.keySet().size() - 1;
		for(int i = 0; i < nonNullEigs; i++)
			lambda *= 1.0 / (1.0 + Math.sqrt(eigs[i]));
		
		return Math.sqrt(1 - lambda);
	}
	
	private RealMatrix removeZeroColumns(RealMatrix base, List<Integer> zeroColumns) {
		int adjustedDim = base.getRowDimension()-zeroColumns.size();
		RealMatrix adjusted = new Array2DRowRealMatrix(adjustedDim, adjustedDim);
		int i = 0, j = 0;
		for(int basei = 0; basei < base.getRowDimension(); basei++) {
			if (zeroColumns.contains(basei))
				continue;
			for(int basej = 0; basej < base.getColumnDimension(); basej++) {
				if (zeroColumns.contains(basej))
					continue;
				adjusted.setEntry(i, j++, base.getEntry(basei, basej));
			}
			i++;
			j = 0;
		}
		return adjusted;
	}

	private List<Integer> findZeroColumns(RealMatrix base) {
		List<Integer> indices = new ArrayList<>();
		for(int i = 0; i < base.getColumnDimension(); i++) {
			if (base.getColumnVector(i).getNorm() == 0)
				indices.add(i);
		}
		return indices;
	}
	/*
	public static void main(String... args) {
		InstanceTemplate template = new InstanceTemplate();
		template.setOption("inputTemplate", new VectorTemplate());
		template.setOption("outputTemplate", new LabelTemplate());
		template.setOption("inputTemplate.dimension", 47);
		template.setOption("outputTemplate.labels.0", "pd");
		template.setOption("outputTemplate.labels.1", "snp");
		
		CSVDatasetBuilder builder = new CSVDatasetBuilder();
		builder.setOption("file", new File("../gamegui/sampledata/HumVar.txt"));
		builder.setOption("template", template);
		builder.setOption("instanceNumber", 3300);
		builder.setOption("shuffle", false);
		
		Dataset dataset = builder.buildDataset();
		Encoder inputEncoder = new VectorEncoder();
		inputEncoder.setOption("template", template.inputTemplate);
		Encoder outputEncoder = new HelperEncoder();
		outputEncoder.setOption("template", template.outputTemplate);
		
		SampleIterator it = dataset.encodedSampleIterator(inputEncoder, outputEncoder, false);
		
		CorrelationRatio ratio = new CorrelationRatio();
		ratio.computeIOCorrelationMatrix(it, 1000);
		ratio.computeSyntheticValues(it, 1000);
		
		System.out.println(toString(ratio.ioCorrelationMatrix));
		
		System.out.println(ratio.syntheticValues);
	}

	private static String toString(RealMatrix M) {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < M.getRowDimension(); i++)
			builder.append(toString(M.getRow(i)) + "\n");
		return builder.toString();
	}

	private static String toString(double[] row) {
		StringBuilder builder = new StringBuilder();
		for(double e: row)
			builder.append(String.format("%10.2f", e));
		return builder.toString();
	}
	*/
}
