package game.core;


public class NoTraining extends TrainingAlgorithm<Block> {

	@Override
	public boolean isCompatible(Block object) {
		return true;
	}

	@Override
	protected void train(Dataset dataset) {
		
	}

	@Override
	public String getTaskDescription() {
		return "no training for " + block;
	}

	@Override
	public String[] getBlockFixedOptions() {
		return new String[0];
	}

}
