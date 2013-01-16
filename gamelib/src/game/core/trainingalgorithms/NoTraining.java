package game.core.trainingalgorithms;

import game.core.Block;
import game.core.Dataset;
import game.core.DatasetTemplate;
import game.core.TrainingAlgorithm;

public class NoTraining extends TrainingAlgorithm<Block> {

	@Override
	protected void train(Dataset dataset) {
		// nothing to do
	}

	@Override
	protected String getTrainingPropertyNames() {
		return "";
	}

	@Override
	protected boolean isCompatible(DatasetTemplate datasetTemplate) {
		return true;
	}

}
