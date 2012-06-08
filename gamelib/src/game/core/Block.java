package game.core;

import game.configuration.ConfigurableList;

public abstract class Block extends LongTask {

	private static final String TRAIN = "training";
	private static final String TRANSFORM = "transforming";
	
	public ConfigurableList<Block> parents = new ConfigurableList<>(this);
	
	public abstract boolean isTrained();
	
	protected abstract double train(Dataset trainingSet);
	
	protected abstract Encoding transform(Object inputData);

	public double startTraining(Dataset trainingSet) {
		return startTask(TRAIN, trainingSet);
	}
	
	public Encoding startTransform(Object inputData) {
		return startTask(TRANSFORM, inputData);
	}
	
	@Override
	protected Object execute(Object... params) {
		if (!isTrained() && getTaskType().equals(TRAIN))
			return train((Dataset)params[0]);
		else if (isTrained() && getTaskType().equals(TRANSFORM))
			return transform(params[0]);
		else
			return null;
	}

}
