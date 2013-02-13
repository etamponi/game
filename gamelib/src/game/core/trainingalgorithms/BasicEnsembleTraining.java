package game.core.trainingalgorithms;

import com.ios.errorchecks.RangeCheck;

import game.core.Dataset;
import game.core.DatasetTemplate;
import game.core.TrainingAlgorithm;
import game.core.blocks.Ensemble;

public class BasicEnsembleTraining extends TrainingAlgorithm<Ensemble> {
	
	@InName
	public double subsetPercent = 1.0;
	
	public BasicEnsembleTraining() {
		addErrorCheck("subsetPercent", new RangeCheck(Double.MIN_VALUE, 1.0));
	}

	@Override
	protected void train(Dataset dataset) {
		double increase = 1.0 / block.classifiers.size();
		for(int i = 0; i < block.classifiers.size(); i++) {
			updateStatus(i*increase, "training classifier " + (i+1) + " of ensemble");
			executeAnotherTaskAndWait((i+1)*increase, block.classifiers.get(i).trainingAlgorithm, dataset.getRandomSubset(subsetPercent));
		}
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
