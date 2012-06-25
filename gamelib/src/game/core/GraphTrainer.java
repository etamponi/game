package game.core;

import game.plugins.constraints.Compatible;

import java.io.File;

public abstract class GraphTrainer extends LongTask implements Compatible<InstanceTemplate> {
	
	public static final String TASKNAME = "graphtraining";
	
	public File graphsDir = new File("graphs/");
	
	protected abstract void trainGraph(Graph graph, Dataset trainingSet);
	
	public <T> T startGraphTraining(Graph graph, Dataset trainingSet) {
		return startTask(TASKNAME, graph);
	}

	@Override
	protected Object execute(Object... params) {
		Graph graph = (Graph)params[0];
		trainGraph(graph, (Dataset)params[1]);
		graph.setTrained();
		if (!graphsDir.exists())
			graphsDir.mkdirs();
		graph.saveConfiguration(graphsDir.getAbsolutePath()+"/"+graph.name+".config.xml");
		return null;
	}

}
