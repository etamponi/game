package game.plugins.algorithms;

import game.core.Block;
import game.core.Dataset;
import game.core.Dataset.SampleIterator;
import game.core.Experiment;
import game.core.Sample;
import game.core.TrainingAlgorithm;
import game.plugins.pipes.Concatenator;
import game.plugins.pipes.FeatureSelection;
import game.plugins.pipes.LinearTransform;
import game.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

import com.ios.ErrorCheck;
import com.ios.errorchecks.PositivenessCheck;
import com.ios.errorchecks.RangeCheck;

public class CanonicalTransform extends TrainingAlgorithm<LinearTransform> {
	
	public double selectionThreshold = 0.66;
	
	public int splits = 3;
	
	public CanonicalTransform() {
		addErrorCheck("selectionThreshold", new RangeCheck(0, 1));
		
		addErrorCheck("splits", new PositivenessCheck(false));
		
		addErrorCheck("block", new ErrorCheck<Block>() {
			private CanonicalTransform wrapper = CanonicalTransform.this;
			@Override
			public String getError(Block value) {
				if (value.getParent(0) != null && value.getParent(0).getFeatureNumber() % (2*wrapper.splits) != 0) {
					return "must be divisible by " + (2*wrapper.splits);
				} else {
					return null;
				}
			}
		});
	}

	@Override
	public boolean isCompatible(Block object) {
		return object instanceof LinearTransform;
	}

	@Override
	protected void train(Dataset dataset) {
		Block inputEncoder = block.getParent(0);
		
		int totalFeatures = inputEncoder.getFeatureNumber();
		int featuresPerSplit = totalFeatures / splits;
		
		RealMatrix transform = new Array2DRowRealMatrix(totalFeatures, totalFeatures);
		
		List<Integer> rowsToRemove = new ArrayList<>();
		
		for(int i = 0; i < splits; i++) {
			int startIndex = i*featuresPerSplit;
			
			FeatureSelection selection = new FeatureSelection();
			selection.parents.add(inputEncoder);
			selection.setContent("mask", computeSplitMask(totalFeatures, featuresPerSplit, startIndex));
			
			transform = transform.add(buildTransform(dataset, selection, startIndex, totalFeatures, rowsToRemove));
		}
		
		System.out.println(rowsToRemove);
		
		int finalFeatures = totalFeatures - rowsToRemove.size();
		int[] cols = new int[totalFeatures];
		for(int i = 0; i < cols.length; i++)
			cols[i] = i;
		int[] rows = new int[finalFeatures];
		int row = 0;
		for(int i = 0; i < totalFeatures; i++) {
			if (!rowsToRemove.isEmpty() && rowsToRemove.get(0) == i) {
				rowsToRemove.remove(0);
				continue;
			} else {
				rows[row++] = i;
			}
		}
		
		transform = transform.getSubMatrix(rows, cols);
		
		block.setContent("transform", transform);
		
		new Matrix(transform.getData()).print(6, 2);
	}
	
	private String computeSplitMask(int totalFeatures, int featuresPerSplit, int startIndex) {
		StringBuilder ret = new StringBuilder();
		for(int i = 0; i < totalFeatures; i++)
			ret.append('0');
		for(int i = 0; i < featuresPerSplit; i++)
			ret.setCharAt(startIndex+i, '1');
		return ret.toString();
	}

	protected RealMatrix buildTransform(Dataset dataset, Block inputEncoder, int subMatrixIndex, int totalFeatures, List<Integer> rowsToRemove) {
		FeatureSelection first = new FeatureSelection();
		first.parents.add(inputEncoder);
		first.mask = generateRandomMask(inputEncoder.getFeatureNumber());
		
		FeatureSelection second = new FeatureSelection();
		second.parents.add(inputEncoder);
		second.mask = invertMask(first.mask);
		
		Concatenator concat = new Concatenator();
		concat.parents.add(first);
		concat.parents.add(second);
		
		int half = first.getFeatureNumber();
		
		RealMatrix cov = computeCovarianceMatrix(dataset.encodedSampleIterator(concat, null, false));
		
		RealMatrix Syy = cov.getSubMatrix(0, half-1, 0, half-1);
		RealMatrix Sxx = cov.getSubMatrix(half, 2*half-1, half, 2*half-1);
		RealMatrix Syx = cov.getSubMatrix(0, half-1, half, 2*half-1);
		RealMatrix Sxy = cov.getSubMatrix(half, 2*half-1, 0, half-1);
		
		RealMatrix Syyinv = new LUDecomposition(Syy).getSolver().getInverse();
		RealMatrix Sxxinv = new LUDecomposition(Sxx).getSolver().getInverse();

		Matrix M1 = new Matrix(Syyinv.multiply(Syx).multiply(Sxxinv).multiply(Sxy).getData());
		Matrix M2 = new Matrix(Sxxinv.multiply(Sxy).multiply(Syyinv).multiply(Syx).getData());

		EigenvalueDecomposition eig1 = M1.eig();
		EigenvalueDecomposition eig2 = M2.eig();
		Matrix A = eig1.getV();
		Matrix B = eig2.getV();

		System.out.println(Arrays.toString(eig1.getRealEigenvalues()));
		System.out.println(Arrays.toString(eig2.getRealEigenvalues()));
		
		Matrix D = eig1.getD();
		for(int i = 0; i < half; i++) {
			if (D.get(i, i) > selectionThreshold)
				rowsToRemove.add(subMatrixIndex+i);
		}
		
		RealMatrix transform = new Array2DRowRealMatrix(totalFeatures, totalFeatures);
		
		int ai = 0, bi = 0;
		for(int j = 0; j < 2*half; j++) {
			if (first.mask.charAt(j) == '1') {
				for(int i = 0; i < half; i++) {
					transform.setEntry(subMatrixIndex+i, subMatrixIndex+j, A.get(ai, i));
				}
				ai++;
			} else {
				for(int i = 0; i < half; i++) {
					transform.setEntry(subMatrixIndex+half+i, subMatrixIndex+j, B.get(bi, i));
				}
				bi++;
			}
		}
		
		return transform;
	}

	private String invertMask(String mask) {
		StringBuilder ret = new StringBuilder(mask);
		for(int i = 0; i < ret.length(); i++)
			ret.setCharAt(i, ret.charAt(i) == '0' ? '1' : '0');
		return ret.toString();
	}

	private String generateRandomMask(int featureNumber) {
		List<Integer> range = Utils.range(0, featureNumber);
		Collections.shuffle(range, Experiment.getRandom());
		StringBuilder ret = new StringBuilder();
		for(int i = 0; i < featureNumber; i++) {
			ret.append('0');
		}
		for(int i = 0; i < featureNumber/2; i++) {
			ret.setCharAt(range.get(i), '1');
		}
		return ret.toString();
	}
	
	public RealMatrix computeCovarianceMatrix(SampleIterator it) {
		List<double[]> X = new ArrayList<>();
		
		NormalDistribution distribution = new NormalDistribution(0, 1e-10);

		while(it.hasNext()) {
			Sample sample = it.next();
			X.add(injectNoise(sample.getEncodedInput().toArray(), distribution));
		}
		
		return new Covariance(X.toArray(new double[][]{})).getCovarianceMatrix();
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
