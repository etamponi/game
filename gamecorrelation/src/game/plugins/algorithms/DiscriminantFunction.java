package game.plugins.algorithms;

import game.core.Block;
import game.core.Dataset;
import game.core.DatasetBuilder;
import game.core.InstanceTemplate;
import game.core.Dataset.SampleIterator;
import game.core.Sample;
import game.core.TrainingAlgorithm;
import game.core.blocks.Encoder;
import game.core.blocks.Pipe;
import game.plugins.datasetbuilders.CSVDatasetBuilder;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.datatemplates.VectorTemplate;
import game.plugins.encoders.IntegerEncoder;
import game.plugins.encoders.VectorEncoder;
import game.plugins.pipes.FeatureSelection;
import game.plugins.pipes.LinearTransform;
import game.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

public class DiscriminantFunction extends TrainingAlgorithm<LinearTransform> {
	
	public int dimensions = 3;

	@Override
	public boolean isCompatible(Block object) {
		return object instanceof LinearTransform;
	}

	public RealMatrix getTransform(Dataset dataset, Block inputEncoder) {
		Encoder outputEncoder = new IntegerEncoder();
		outputEncoder.setContent("template", dataset.getTemplate().outputTemplate);
		
		int inputDim = inputEncoder.getFeatureNumber();
		
		Map<Double, Integer> n_y = new HashMap<>();
		Map<Double, MultivariateSummaryStatistics> stat_y = new HashMap<>();
		List<RealMatrix> x = new ArrayList<>();
		MultivariateSummaryStatistics stat = new MultivariateSummaryStatistics(inputDim, false);
		
		SampleIterator it = dataset.encodedSampleIterator(inputEncoder, outputEncoder, false);
		
		NormalDistribution distribution = new NormalDistribution(0, 1e-6);
		
		while (it.hasNext()) {
			Sample sample = it.next();
			double[] input = sample.getEncodedInput().toArray();
			double output = sample.getEncodedOutput().getEntry(0);
			if (!n_y.containsKey(output)) {
				n_y.put(output, 0);
				stat_y.put(output, new MultivariateSummaryStatistics(inputDim, false));
			}
			
			injectNoise(input, distribution);
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
		
		Matrix JE = new Matrix(E.getData());
		Matrix JH = new Matrix(H.getData());
		
//		Matrix Uinv = JE.chol().getL().transpose().inverse();
		
		Matrix M = JE.inverse().times(JH);
//		Matrix M = Uinv.transpose().times(JH).times(Uinv);
		
		EigenvalueDecomposition dec = M.eig();
		Matrix D = dec.getD();
		
//		D.print(6, 2);
//		dec.getV().print(6, 2);
		
//		double[][] cols = Uinv.times(dec.getV()).transpose().getArray();
		double[][] cols = dec.getV().transpose().getArray();
		
		RealMatrix transform = new Array2DRowRealMatrix(dimensions, inputDim);

		for(int i = 0; i < transform.getRowDimension(); i++) {
			int index = findIndex(D);
			D.set(index, index, 0);
			transform.setRow(i, cols[index]);
		}

//		new Matrix(transform.getData()).times(100).print(6, 2);
		
		return transform;
	}
		
	@Override
	protected void train(Dataset dataset) {
		block.setContent("transform", getTransform(dataset, block.getParent(0)));
	}
	
	private int findIndex(Matrix m) {
		int ret = 0;
		for(int i = 1; i < m.getRowDimension(); i++) {
			if (m.get(i, i) > m.get(ret, ret)) {
				ret = i;
			}
		}
		return ret;
	}

	private double[] injectNoise(double[] v, NormalDistribution distribution) {
//		return v;
		for(int i = 0; i < v.length; i++)
			v[i] = v[i] + distribution.sample();
		return v;
	}

	@Override
	protected String getManagedPropertyNames() {
		return "transform";
	}
	
	public static void main(String... args) {
		InstanceTemplate template = new InstanceTemplate();
		template.inputTemplate = new VectorTemplate();
		template.inputTemplate.setContent("dimension", 47);
		template.outputTemplate = new LabelTemplate();
		template.outputTemplate.getContent("labels", List.class).add("pd");
		template.outputTemplate.getContent("labels", List.class).add("snp");
		
		DatasetBuilder builder = new CSVDatasetBuilder();
		builder.setContent("template", template);
		builder.setContent("file", new File("../gamegui/sampledata/HumVar.txt"));
		builder.setContent("instanceNumber", 5000);
		
		Dataset dataset = builder.buildDataset();
		
		Encoder inputEncoder = new VectorEncoder();
		inputEncoder.setContent("template", template.inputTemplate);
		Pipe featureSelection = new FeatureSelection();
		featureSelection.setContent("mask", "0000000000" + generateRandomMask(36, 36) + "0");
		featureSelection.parents.add(inputEncoder);
		
		LinearTransform transform = new LinearTransform();
		transform.parents.add(featureSelection);
		transform.setContent("trainingAlgorithm", new DiscriminantFunction());
		
		transform.trainingAlgorithm.execute(dataset);
	}
	
	private static String generateRandomMask(int featureNumber, int n) {
		List<Integer> range = Utils.range(0, featureNumber);
		Collections.shuffle(range);
		StringBuilder ret = new StringBuilder();
		for(int i = 0; i < featureNumber; i++) {
			ret.append('0');
		}
		for(int i = 0; i < n; i++) {
			ret.setCharAt(range.get(i), '1');
		}
		return ret.toString();
	}

}
