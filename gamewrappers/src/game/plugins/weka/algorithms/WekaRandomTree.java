package game.plugins.weka.algorithms;

import game.core.Dataset;
import game.core.Experiment;
import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomTree;
import weka.core.Instances;

public class WekaRandomTree extends WekaTrainingAlgorithm {
	
	public int featuresPerNode = 0;

	@Override
	protected Classifier setupInternal(Dataset dataset, Instances instances) {
		RandomTree tree = new RandomTree();
		
		tree.setKValue(featuresPerNode);
		tree.setSeed(Experiment.getRandom().nextInt());
		
		return tree;
	}

}
