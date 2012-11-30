package game.plugins.algorithms;

import game.core.Block;
import game.core.Dataset;
import game.core.Dataset.SampleIterator;
import game.core.Sample;
import game.plugins.classifiers.Criterion;
import game.plugins.encoders.BooleanEncoder;
import game.plugins.pipes.FeatureSelection;
import game.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class DiscriminantCriterion extends Criterion {
	
	private RealVector transform;
	
	private double threshold;
	
	public DiscriminantCriterion(Dataset dataset, Block inputEncoder, int features) {
		BooleanEncoder outputEncoder = new BooleanEncoder();
		outputEncoder.setContent("template", dataset.getTemplate().outputTemplate);
		
		int originalFeatures = inputEncoder.getFeatureNumber();
		
		if (features < originalFeatures) {
			Block ancestor = inputEncoder;
			inputEncoder = new FeatureSelection();
			inputEncoder.setContent("mask", generateRandomMask(ancestor.getFeatureNumber(), features));
			inputEncoder.parents.add(ancestor);
		}
		
		List<RealVector> yp_list = new ArrayList<>();
		List<RealVector> yn_list = new ArrayList<>();
		
		SampleIterator it = dataset.encodedSampleIterator(inputEncoder, outputEncoder, false);
		int count = 0;
//		NormalDistribution distribution = new NormalDistribution(0, 1e-6);
		while(it.hasNext()) {
			Sample sample = it.next();
			RealVector input = /*injectNoise(*/sample.getEncodedInput()/*, distribution)*/;
			if (sample.getOutput().equals(outputEncoder.positiveLabel())) {
				yp_list.add(input);
			} else {
				yn_list.add(input);
			}
			count++;
		}

		assert(count - 2 > features);
		
		RealVector yp_mean = evaluateMean(yp_list, features);
		RealVector yn_mean = evaluateMean(yn_list, features);
		/*
		RealMatrix Wp = evaluateW(yp_list, yp_mean);
		RealMatrix Wn = evaluateW(yn_list, yn_mean);
		
		Matrix Spl = new Matrix(Wp.add(Wn).scalarMultiply(1.0 / (count - 2)).getData());
		RealMatrix Splinv = new Array2DRowRealMatrix(Spl.inverse().getArray());
		
		RealVector transform = Splinv.operate(yp_mean.subtract(yn_mean));
		*/
		
		DiscriminantFunction discriminant = new DiscriminantFunction();
		discriminant.dimensions = 1;
		
		transform = discriminant.getTransform(dataset, inputEncoder).getRowVector(0);
		threshold = 0.5 * transform.dotProduct(yp_mean.add(yn_mean));
		
		if (features < originalFeatures) {
			transform = adjust(transform, inputEncoder.getContent("mask", String.class));
		}
	}
/*
	private static RealVector injectNoise(RealVector v, NormalDistribution distribution) {
		RealVector ret = new ArrayRealVector(v.getDimension());
		for(int i = 0; i < v.getDimension(); i++)
			ret.setEntry(i, v.getEntry(i) + distribution.sample());
		return ret;
	}
*/
	private static RealVector adjust(RealVector transform, String mask) {
		RealVector ret = new ArrayRealVector();
		int i = 0;
		for(char c: mask.toCharArray()) {
			if (c == '1')
				ret = ret.append(transform.getEntry(i++));
			else
				ret = ret.append(0);
		}
		return ret;
	}
/*
	private static RealMatrix evaluateW(List<RealVector> y_list, RealVector y_mean) {
		RealMatrix ret = new Array2DRowRealMatrix(y_mean.getDimension(), y_mean.getDimension());
		for(RealVector y: y_list) {
			RealMatrix temp = new Array2DRowRealMatrix(y.subtract(y_mean).toArray());
			ret = ret.add(temp.multiply(temp.transpose()));
		}
		return ret;
	}
*/
	private static RealVector evaluateMean(List<RealVector> y_list, int features) {
		if (y_list.isEmpty())
			return new ArrayRealVector(features);
		Iterator<RealVector> it = y_list.iterator();
		RealVector ret = it.next();
		while(it.hasNext())
			ret = ret.add(it.next());
		ret.mapDivideToSelf(y_list.size());
		return ret;
	}

	@Override
	public int decide(RealVector input) {
		if (transform.dotProduct(input) <= threshold)
			return 0;
		else
			return 1;
	}
	
	private static String generateRandomMask(int total, int selected) {
		List<Integer> range = Utils.range(0, total);
		Collections.shuffle(range);
		StringBuilder ret = new StringBuilder();
		for(int i = 0; i < total; i++) {
			ret.append('0');
		}
		for(int i = 0; i < selected; i++) {
			ret.setCharAt(range.get(i), '1');
		}
		return ret.toString();
	}
	
}
