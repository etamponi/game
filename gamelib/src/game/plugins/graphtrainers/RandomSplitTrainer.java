/*******************************************************************************
 * Copyright (c) 2012 Emanuele Tamponi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Emanuele Tamponi - initial API and implementation
 ******************************************************************************/
package game.plugins.graphtrainers;

import game.configuration.errorchecks.RangeCheck;
import game.core.Block;
import game.core.Dataset;
import game.core.GraphTrainer;
import game.core.blocks.Graph;

import java.util.HashSet;
import java.util.Set;

public class RandomSplitTrainer extends GraphTrainer {
	
	public double splitPercent = 1.0;
	
	public RandomSplitTrainer() {
		setOptionChecks("splitPercent", new RangeCheck(0.05, 1.00));
	}

	@Override
	public boolean isCompatible(Graph graph) {
		return true;
	}

	@Override
	protected void trainGraph(Graph graph, Dataset trainingSet) {
		recursivelyTrainGraph(graph.outputClassifier, trainingSet, 1.0/blocksToTrain(graph));
	}

	private void recursivelyTrainGraph(Block current, Dataset trainingSet, double increase) {
		for (Block parent: current.parents.getList(Block.class))
			recursivelyTrainGraph(parent, trainingSet, increase);

		if (!current.isTrained()) {
			startAnotherTaskAndWait(getCurrentPercent()+increase, current, trainingSet.getRandomSubset(splitPercent));
		}
	}
	
	private int blocksToTrain(Graph graph) {
		Set<Block> blocks = new HashSet<>();
		countBlocks(graph.outputClassifier, blocks);
		int count = 0;
		for(Block block: blocks)
			if (!block.isTrained()) count++;
		return count;
	}
	
	private void countBlocks(Block current, Set<Block> blocks) {
		if (!blocks.contains(current)) {
			blocks.add(current);
			for(Block parent: current.parents.getList(Block.class))
				countBlocks(parent, blocks);
		}
	}

	@Override
	public String getTaskDescription() {
		return String.format("graph training with random splits (dataset percent per split: %.2f%%)", splitPercent*100); 
	}

}
