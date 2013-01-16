package game.plugins.trainingalgorithms;

import com.ios.triggers.MasterSlaveTrigger;

import game.core.Dataset;
import game.core.DatasetTemplate;
import game.core.TrainingAlgorithm;
import game.plugins.blocks.pipes.VectorToLabel;
import game.plugins.datatemplates.LabelTemplate;

public class TargetLabelDecoder extends TrainingAlgorithm<VectorToLabel> {
	
	public TargetLabelDecoder() {
		addTrigger(new MasterSlaveTrigger(this, "block.datasetTemplate.targetTemplate.0.labels", "block.labels"));
	}

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
		return datasetTemplate != null && datasetTemplate.targetTemplate != null
				&& datasetTemplate.targetTemplate.isSingletonTemplate(LabelTemplate.class);
	}

}
