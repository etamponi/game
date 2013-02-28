package game.core.trainingalgorithms;

import game.core.Block;
import game.core.Dataset;
import game.core.DatasetTemplate;
import game.core.TrainingAlgorithm;
import game.core.blocks.ClassifierPipeline;
import game.core.blocks.Pipeline;

import java.util.List;

public class PipelineTraining extends TrainingAlgorithm<Block> {

	@Override
	protected void train(Dataset dataset) {
		List<Block> blocks = block.getContent("blocks");
		double increase = 1.0 / blocks.size();
		
		for(int i = 0; i < blocks.size(); i++) {
			Block b = blocks.get(i);
			updateStatus(i*increase, "training block " + (i+1) + " of pipeline");
			executeAnotherTaskAndWait((i+1)*increase, b.trainingAlgorithm, dataset);
			dataset = dataset.apply(b);
		}
	}

	@Override
	protected String getTrainingPropertyNames() {
		return "";
	}

	@Override
	public String compatibilityError(Block block) {
		if (block instanceof Pipeline || block instanceof ClassifierPipeline)
			return null;
		else
			return "compatible only with Pipeline & ClassifierPipeline";
	}

	@Override
	protected String compatibilityError(DatasetTemplate datasetTemplate) {
		return null;
	}

}
