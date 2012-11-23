package game.plugins.weka.algorithms;

import game.core.Dataset;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;

public class WekaJ48 extends WekaTrainingAlgorithm {
	
	public boolean unpruned = false;
	
	public boolean binarySplits = false;

	@Override
	protected Classifier setupInternal(Dataset dataset, Instances instances) {
		J48 tree = new J48();
		
		tree.setBinarySplits(binarySplits);
		tree.setUnpruned(unpruned);
		
		return tree;
	}

}
