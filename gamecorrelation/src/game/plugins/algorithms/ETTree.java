package game.plugins.algorithms;

import game.core.Block;
import game.core.Dataset;
import game.core.Dataset.SampleIterator;
import game.core.Sample;
import game.plugins.classifiers.Criterion;
import game.plugins.encoders.BooleanEncoder;
import game.plugins.pipes.FeatureSelection;
import game.utils.Utils;

import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class ETTree extends C45Like {
	
	public int minimumSize = 300;
	
	public int featuresPerDiscriminant = 0;
	
	@Override
	public boolean isCompatible(Block object) {
		return super.isCompatible(object) && object.getContent("template.outputTemplate.labels", List.class).size() == 2;
	}
	
	private double getThreshold(RealVector transform, Block inputEncoder, Dataset dataset) {
		BooleanEncoder outputEncoder = new BooleanEncoder();
		outputEncoder.setContent("template", dataset.getTemplate().outputTemplate);
		
		double zp = 0; double cp = 0;
		double zn = 0; double cn = 0;
		
		SampleIterator it = dataset.encodedSampleIterator(inputEncoder, outputEncoder, false);
		while(it.hasNext()) {
			Sample sample = it.next();
			double z = transform.dotProduct(sample.getEncodedInput());
			if (sample.getOutput().equals(outputEncoder.positiveLabel())) {
				zp += z;
				cp++;
			} else {
				zn += z;
				cn++;
			}
		}
		
		zp = zp / cp;
		zn = zn / cn;
		
		return (zp + zn)/2;
	}

	@Override
	protected Criterion bestCriterion(Dataset dataset) {
		if (dataset.size() >= minimumSize) {
			Block inputEncoder = block.getParent();
			
			FeatureSelection selection = new FeatureSelection();
			selection.setContent("mask", generateRandomMask(inputEncoder.getFeatureNumber(),
					featuresPerDiscriminant == 0 ? inputEncoder.getFeatureNumber() : featuresPerDiscriminant));
			selection.parents.add(inputEncoder);
			
			DiscriminantFunction discriminant = new DiscriminantFunction();
			discriminant.dimensions = 1;
			
			RealVector transform = discriminant.getTransform(dataset, selection).getRowVector(0);
			double threshold = getThreshold(transform, selection, dataset);
			
			DiscriminantCriterion criterion = new DiscriminantCriterion(adjust(transform, selection.mask), threshold);
		
			return criterion;
		} else {
			return super.bestCriterion(dataset);
		}
	}

	private RealVector adjust(RealVector transform, String mask) {
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

	@Override
	protected String getManagedPropertyNames() {
		return "root";
	}
	
	private static String generateRandomMask(int featureNumber, int n) {
		List<Integer> range = Utils.range(0, featureNumber);
		if (n < featureNumber)
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
