package game.plugins.graphtrainers;

import game.core.Block;
import game.core.Dataset;
import game.core.Graph;
import game.core.GraphTrainer;
import game.core.InstanceTemplate;

public class SimpleTrainer extends GraphTrainer {

	@Override
	public boolean isCompatible(InstanceTemplate object) {
		return true;
	}

	@Override
	protected Object trainGraph(Graph graph, Dataset trainingSet) {
		recursivelyTrainGraph(graph.outputClassifier, trainingSet);
		
		return null;
	}

	private void recursivelyTrainGraph(Block current, Dataset trainingSet) {
		// TODO use updateStatus and startAnotherTaskAndWait
		
		for (Block parent: current.parents.getList(Block.class)) {
			recursivelyTrainGraph(parent, trainingSet);
		}
		if (!current.isTrained())
			current.startTraining(trainingSet);
	}

}
