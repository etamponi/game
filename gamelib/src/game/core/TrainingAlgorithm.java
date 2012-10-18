package game.core;

import game.configuration.errorchecks.CompatibilityCheck;
import game.plugins.constraints.Compatible;

public abstract class TrainingAlgorithm<B extends Block> extends LongTask<Void, Dataset> implements Compatible<Block> {
	
	public B block;
	
	public TrainingAlgorithm() {
		setOptionChecks("block", new CompatibilityCheck(this));
	}
	
	protected abstract void train(Dataset dataset);
	
	protected abstract String[] getBlockFixedOptions();

	@Override
	public Void execute(Dataset dataset) {
		if (!block.trained)
			train(dataset);
		block.trained = true;
		
		return null;
	}

}
