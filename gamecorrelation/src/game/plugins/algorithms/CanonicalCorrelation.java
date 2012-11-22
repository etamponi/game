package game.plugins.algorithms;

import game.core.Block;
import game.core.Dataset;
import game.core.Dataset.SampleIterator;
import game.core.Sample;
import game.core.TrainingAlgorithm;
import game.plugins.pipes.Concatenator;
import game.plugins.pipes.FeatureSelection;
import game.plugins.pipes.LinearTransform;
import game.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;

import Jama.Matrix;

import com.ios.ErrorCheck;

public class CanonicalCorrelation extends TrainingAlgorithm<LinearTransform> {
	
	public CanonicalCorrelation() {
		addErrorCheck("block", new ErrorCheck<Block>() {

			@Override
			public String getError(Block value) {
				if (value.getParent(0) != null && value.getParent(0).getFeatureNumber() % 2 != 0) {
					return "must output an even feature number";
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
		
		RealMatrix Syy = cov.getSubMatrix(0, 0, half-1, half-1);
		RealMatrix Sxx = cov.getSubMatrix(half, half, 2*half-1, 2*half-1);
		RealMatrix Syx = cov.getSubMatrix(0, half, half-1, 2*half-1);
		RealMatrix Sxy = cov.getSubMatrix(half, 0, 2*half-1, half-1);
		
		RealMatrix Syyinv = new LUDecomposition(Syy).getSolver().getInverse();
		RealMatrix Sxxinv = new LUDecomposition(Sxx).getSolver().getInverse();

		Matrix M1 = new Matrix(Syyinv.multiply(Syx).multiply(Sxxinv).multiply(Sxy).getData());
		Matrix M2 = new Matrix(Sxxinv.multiply(Sxy).multiply(Syyinv).multiply(Syx).getData());
		
		Matrix A = M1.eig().getV();
		Matrix B = M2.eig().getV();
	}

	private String invertMask(String mask) {
		StringBuilder ret = new StringBuilder(mask);
		for(int i = 0; i < ret.length(); i++)
			ret.setCharAt(i, ret.charAt(i) == '0' ? '1' : '0');
		return ret.toString();
	}

	private String generateRandomMask(int featureNumber) {
		List<Integer> range = Utils.range(0, featureNumber);
		Collections.shuffle(range);
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

		while(it.hasNext()) {
			Sample sample = it.next();
			X.add(sample.getEncodedInput().toArray());
		}
		
		return new Covariance(X.toArray(new double[][]{})).getCovarianceMatrix();
	}

	@Override
	protected String getManagedPropertyNames() {
		return "transform";
	}

}
