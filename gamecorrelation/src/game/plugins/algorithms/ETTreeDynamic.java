package game.plugins.algorithms;

import game.core.Block;
import game.core.Dataset;
import game.plugins.classifiers.Criterion;
import game.utils.Utils;

import java.util.List;

public class ETTreeDynamic extends C45Like {
	
	public int minimumSize = 100;
	
	public int featuresPerDiscriminant = 0;
	
	@Override
	public boolean isCompatible(Block object) {
		return super.isCompatible(object) && object.getContent("template.outputTemplate.labels", List.class).size() == 2;
	}
/*
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
*/
	@Override
	protected Criterion bestCriterion(Dataset dataset) {
		Block inputEncoder = block.getParent();
		
		CriterionWithGain ret = new CriterionWithGain(null, 0);
		
		if (dataset.size() >= minimumSize) {
			/*
			FeatureSelection selection = new FeatureSelection();
			selection.setContent("mask", generateRandomMask(inputEncoder.getFeatureNumber(),
					featuresPerDiscriminant == 0 ? inputEncoder.getFeatureNumber() : featuresPerDiscriminant));
			selection.parents.add(inputEncoder);
			
			DiscriminantFunction discriminant = new DiscriminantFunction();
			discriminant.dimensions = 1;
			
			RealVector transform = discriminant.getTransform(dataset, selection).getRowVector(0);
			double threshold = getThreshold(transform, selection, dataset);
			
			DiscriminantCriterion criterion = new DiscriminantCriterion(adjust(transform, selection.mask), threshold);
			*/
			DiscriminantCriterion criterion = new DiscriminantCriterion(dataset, inputEncoder,
					featuresPerDiscriminant == 0 ? inputEncoder.getFeatureNumber() : featuresPerDiscriminant);
			
			double gain = information(dataset) + gain(split(dataset, criterion));
		
			ret = new CriterionWithGain(criterion, gain);
		}
		
		List<Integer> range = Utils.range(0, block.getParent().getFeatureNumber());
		
		List<Integer> possibleFeatures = featuresPerNode == 0 ? range : selector.select(featuresPerNode, dataset);
		
		for(int feature: possibleFeatures) {
			CriterionWithGain current = bestCriterionFor(feature, dataset);
			if (current.getGain() > ret.getGain())
				ret = current;
		}
		
		return ret.getCriterion();
	}

	@Override
	protected String getManagedPropertyNames() {
		return "root";
	}

}
