package game.plugins.algorithms;

import game.core.Block;
import game.core.Dataset;
import game.core.Dataset.SampleIterator;
import game.core.Sample;
import game.core.TrainingAlgorithm;
import game.plugins.classifiers.KNNClassifier;
import game.plugins.classifiers.KNNClassifier.ReferenceSample;

public class SimpleKNNTraining extends TrainingAlgorithm<KNNClassifier> {

	@Override
	public boolean isCompatible(Block object) {
		return object instanceof KNNClassifier;
	}

	@Override
	protected void train(Dataset trainingSet) {
		SampleIterator it = trainingSet.encodedSampleIterator(block.getParent(0), block.outputEncoder, false);
		while(it.hasNext()) {
			Sample sample = it.next();
			block.reference.add(new ReferenceSample(sample.getEncodedInput(), sample.getEncodedOutput()));
		}
	}

	@Override
	public String getTaskDescription() {
		return "training KNNClassifier using whole training set";
	}

	@Override
	public String[] getBlockFixedOptions() {
		return new String[]{"reference"};
	}

}
