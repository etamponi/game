package game.plugins.weka.algorithms;

import java.util.Random;

import game.core.Dataset;
import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomTree;
import weka.core.Instances;

public class WekaRandomTree extends WekaTrainingAlgorithm {
	
	public int featuresPerNode = 0;
	
	public boolean randomSeed = true;

	@Override
	protected Classifier setupInternal(Dataset dataset, Instances instances) {
		RandomTree tree = new RandomTree();
		
		tree.setKValue(featuresPerNode);
		if (randomSeed)
			tree.setSeed(new Random().nextInt());
		
		return tree;
	}

}
