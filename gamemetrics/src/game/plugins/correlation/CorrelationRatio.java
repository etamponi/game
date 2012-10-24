package game.plugins.correlation;

import game.core.Dataset.SampleIterator;
import game.core.Sample;
import game.utils.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics;

import Jama.Matrix;

public class CorrelationRatio extends CorrelationCoefficient {
	
	public boolean unbiased = false;
	
	public double zeroThreshold = 1e-12;

	@Override
	public RealMatrix computeInputCorrelationMatrix(SampleIterator it) {
		return null;
	}

	@Override
	public RealMatrix computeIOCorrelationMatrix(SampleIterator it) {
		it.reset();
		Sample sample = it.next();
		int inputDim = sample.getEncodedInput().getDimension();
		int outputDim = sample.getEncodedOutput().getDimension();
		
		RealMatrix M = new Array2DRowRealMatrix(inputDim, outputDim);
		
		for(int i = 0; i < inputDim; i++) {
			for(int j = 0; j < outputDim; j++) {
				it.reset();
				M.setEntry(i, j, correlationRatio(it, i, j));
			}
		}
		
		return M;
	}

	private double correlationRatio(SampleIterator it, int in, int out) {
		Map<Double, Integer> n_y = new HashMap<>();
		Map<Double, RealVector> x_y = new HashMap<>();
		RealVector x = new ArrayRealVector();
		
		while(it.hasNext()) {
			Sample sample = it.next();
			double input = sample.getEncodedInput().getEntry(in);
			double output = sample.getEncodedOutput().getEntry(out);
			if (!n_y.containsKey(output)) {
				n_y.put(output, 0);
				x_y.put(output, new ArrayRealVector());
			}
			
			n_y.put(output, n_y.get(output)+1);
			x_y.put(output, x_y.get(output).append(input));
			x = x.append(input);
		}
		
		double x_mean = StatUtils.mean(x.toArray());
		Map<Double, Double> x_y_mean = new HashMap<>();
		for(Entry<Double, RealVector> entry: x_y.entrySet()) {
			x_y_mean.put(entry.getKey(), StatUtils.mean(entry.getValue().toArray()));
		}
		
		double numerator = 0;
		for(double key: n_y.keySet()) {
			numerator += n_y.get(key) * (x_y_mean.get(key) - x_mean) * (x_y_mean.get(key) - x_mean);
		}
		double denominator = 0;
		for(double e: x.toArray()) {
			denominator += (e - x_mean) * (e - x_mean);
		}
		
		double eta = denominator == 0 ? 0 : Math.sqrt(numerator / denominator);
		
		return eta;
	}

	@Override
	public RealVector computeSyntheticValues(SampleIterator it) {
		it.reset();
		Sample sample = it.next();
		int inputDim = sample.getEncodedInput().getDimension();
		int outputDim = sample.getEncodedOutput().getDimension();
		
		RealVector v = new ArrayRealVector(outputDim);
		for(int out = 0; out < outputDim; out++) {
			it.reset();
			double eta = generalizedCorrelationRatio(it, inputDim, out);
			if (eta < 0)
				return null;
			v.setEntry(out, eta);
		}

		return v;
	}
	
	private double generalizedCorrelationRatio(SampleIterator it, int inputDim, int out) {
		Map<Double, Integer> n_y = new HashMap<>();
		Map<Double, MultivariateSummaryStatistics> stat_y = new HashMap<>();
		List<RealMatrix> x = new ArrayList<>();
		MultivariateSummaryStatistics stat = new MultivariateSummaryStatistics(inputDim, unbiased);
		
		while(it.hasNext()) {
			Sample sample = it.next();
			double[] input = sample.getEncodedInput().toArray();
			double output = sample.getEncodedOutput().getEntry(out);
			if (!n_y.containsKey(output)) {
				n_y.put(output, 0);
				stat_y.put(output, new MultivariateSummaryStatistics(inputDim, unbiased));
			}
			
			n_y.put(output, n_y.get(output)+1);
			stat_y.get(output).addValue(input);
			x.add(new Array2DRowRealMatrix(input));
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
		H = temp.subtract(x_sum.multiply(x_sum.transpose()).scalarMultiply(1.0 / x.size()));
		
		RealMatrix E = new Array2DRowRealMatrix(inputDim, inputDim);
		for(RealMatrix m: x) {
			E = E.add(m.multiply(m.transpose()));
		}
		E = E.subtract(temp);
		
		List<Integer> zeroColumns = findZeroColumns(E);
		E = removeZeroColumns(E, zeroColumns);
		H = removeZeroColumns(H, zeroColumns);
		
		Matrix JE = new Matrix(E.getData());
		Matrix JH = new Matrix(H.getData());
		
		if (JE.rank() < JE.getRowDimension()) {
			Log.write(this, "Some error occurred (E matrix is singular)");
			return -1;
		} else {
			Matrix L = JE.inverse().times(JH);
			double[] eigs = L.eig().getRealEigenvalues();
			Arrays.sort(eigs);
			
			double lambda = 1;
			int nonNullEigs = n_y.keySet().size() - 1;
			for(int i = eigs.length-nonNullEigs; i < eigs.length; i++) {
				if (Math.abs(eigs[i]) < zeroThreshold) {
					Log.write(this, "Some error occurred (E matrix has too many null eigenvalues)");
					return -1;
				}
				lambda *= 1.0 / (1.0 + eigs[i]);
			}
			
			return Math.sqrt(1 - lambda);
		}
	}

	private RealMatrix removeZeroColumns(RealMatrix base, List<Integer> zeroColumns) {
		int adjustedDim = base.getRowDimension()-zeroColumns.size();
		if (adjustedDim == 0)
			return base;
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
	
}
