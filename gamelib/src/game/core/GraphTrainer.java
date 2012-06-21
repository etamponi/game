package game.core;

import game.plugins.constraints.Compatible;

public abstract class GraphTrainer extends LongTask implements Compatible<InstanceTemplate> {
	
	public static final String TASKNAME = "training";
	
	protected abstract Object trainGraph(Graph graph, Dataset trainingSet);
	
	public <T> T startGraphTraining(Graph graph, Dataset trainingSet) {
		return startTask(TASKNAME, graph);
	}

	@Override
	protected Object execute(Object... params) {
		return trainGraph((Graph)params[0], (Dataset)params[1]);
	}

}
