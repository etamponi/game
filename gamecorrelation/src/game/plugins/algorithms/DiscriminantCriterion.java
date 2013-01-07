package game.plugins.algorithms;

import game.core.Block;
import game.core.Dataset;
import game.core.Dataset.SampleIterator;
import game.core.Sample;
import game.plugins.classifiers.Criterion;
import game.plugins.encoders.BooleanEncoder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class DiscriminantCriterion extends Criterion {
	
	private RealVector transform;
	
	private double threshold;
	
	public DiscriminantCriterion(Dataset dataset, Block inputEncoder) {
		BooleanEncoder outputEncoder = new BooleanEncoder();
		outputEncoder.setContent("template", dataset.getTemplate().outputTemplate);
		
		List<RealVector> yp_list = new ArrayList<>();
		List<RealVector> yn_list = new ArrayList<>();
		
		SampleIterator it = dataset.encodedSampleIterator(inputEncoder, outputEncoder, false);
		int count = 0;
		while(it.hasNext()) {
			Sample sample = it.next();
			RealVector input = sample.getEncodedInput();
			if (sample.getOutput().equals(outputEncoder.positiveLabel())) {
				yp_list.add(input);
			} else {
				yn_list.add(input);
			}
			count++;
		}

		assert(count - 2 > inputEncoder.getFeatureNumber());
		
		RealVector yp_mean = evaluateMean(yp_list, inputEncoder.getFeatureNumber());
		RealVector yn_mean = evaluateMean(yn_list, inputEncoder.getFeatureNumber());
		
		DiscriminantFunction discriminant = new DiscriminantFunction();
		discriminant.dimensions = 1;
		
		transform = discriminant.getTransform(dataset, inputEncoder).getRowVector(0);
		threshold = 0.5 * transform.dotProduct(yp_mean.add(yn_mean));
	}

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

}
