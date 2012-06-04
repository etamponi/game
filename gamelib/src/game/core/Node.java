package game.core;

import game.configuration.errorchecks.ListOfConfigurablesCheck;

import java.util.LinkedList;

public abstract class Node extends LongTask {

	private static final String TRAIN = "training";
	private static final String TRANSFORM = "transforming";
	
	public LinkedList<Node> parents = new LinkedList<>();
	
	public Node() {
		addOptionChecks("parents", new ListOfConfigurablesCheck());
	}
	
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
