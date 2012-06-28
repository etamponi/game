package game.plugins.graphtrainers;

import game.core.Block;
import game.core.Dataset;
import game.core.Graph;
import game.core.GraphTrainer;
import game.core.InstanceTemplate;

import java.util.HashSet;
import java.util.Set;

public class SimpleTrainer extends GraphTrainer {
	
	private int total;

	@Override
	public boolean isCompatible(InstanceTemplate object) {
		return true;
	}

	@Override
	protected void trainGraph(Graph graph, Dataset trainingSet) {
		total = blocksToTrain(graph);
		recursivelyTrainGraph(graph.outputClassifier, trainingSet);
	}

	private void recursivelyTrainGraph(Block current, Dataset trainingSet) {
		// TODO use updateStatus and startAnotherTaskAndWait
		
		for (Block parent: current.parents.getList(Block.class))
			recursivelyTrainGraph(parent, trainingSet);

		if (!current.isTrained()) {
			double increase = 1.0 / total;
			startAnotherTaskAndWait(getCurrentPercent()+increase, current, Block.TRAININGTASK, trainingSet);
		}
	}
	
	private int blocksToTrain(Graph graph) {
		Set<Block> blocks = new HashSet<>();
		countUntrainedBlocks(graph.outputClassifier, blocks);
		return blocks.size();
	}
	
	private void countUntrainedBlocks(Block current, Set<Block> blocks) {
		if (!blocks.contains(current)) {
			blocks.add(current);
			for(Block parent: current.parents.getList(Block.class))
				countUntrainedBlocks(parent, blocks);
		}
	}

}
