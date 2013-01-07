package game.plugins.algorithms;

import game.core.Block;
import game.core.Dataset;
import game.core.Experiment;
import game.core.Dataset.SampleIterator;
import game.core.Sample;
import game.core.TrainingAlgorithm;
import game.core.blocks.Encoder;
import game.plugins.encoders.IntegerEncoder;
import game.plugins.pipes.LinearTransform;

import java.util.ArrayList;
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
	
	public int dimensions = 1;

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
		distribution.reseedRandomGenerator(Experiment.getRandom().nextLong());
		
		while (it.hasNext()) {
			Sample sample = it.next();
			
			double[] input = sample.getEncodedInput().toArray();
			injectNoise(input, distribution);
			x.add(new Array2DRowRealMatrix(input));
			stat.addValue(input);
			
			double output = sample.getEncodedOutput().getEntry(0);
			
			if (!n_y.containsKey(output)) {
				n_y.put(output, 0);
				stat_y.put(output, new MultivariateSummaryStatistics(inputDim, false));
			}
			
			n_y.put(output, n_y.get(output)+1);
			
			stat_y.get(output).addValue(input);
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
		Matrix M = JE.inverse().times(JH);
		
		EigenvalueDecomposition dec = M.eig();
		Matrix D = dec.getD();

		double[][] cols = dec.getV().transpose().getArray();
		
		RealMatrix transform = new Array2DRowRealMatrix(dimensions, inputDim);

		for(int i = 0; i < transform.getRowDimension(); i++) {
			int index = findIndex(D);
			D.set(index, index, -1);
			transform.setRow(i, cols[index]);
		}
		
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
		for(int i = 0; i < v.length; i++)
			v[i] = v[i] + distribution.sample();
		return v;
	}

	@Override
	protected String getManagedPropertyNames() {
		return "transform";
	}

}
