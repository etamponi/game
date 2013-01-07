package game.plugins.algorithms;

import game.core.Block;
import game.core.Dataset;
import game.plugins.classifiers.Criterion;
import game.utils.Utils;

import java.util.List;

public class ETTreeDynamic extends C45Like {
	
	public int minimumSize = 100;
	
	@Override
	public boolean isCompatible(Block object) {
		return super.isCompatible(object) && object.getContent("template.outputTemplate.labels", List.class).size() == 2;
	}

	@Override
	protected Criterion bestCriterion(Dataset dataset) {
		Block inputEncoder = block.getParent();
		
		CriterionWithGain ret = new CriterionWithGain(null, 0);
		
		if (dataset.size() >= minimumSize) {
			DiscriminantCriterion criterion = new DiscriminantCriterion(dataset, inputEncoder);
			
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
