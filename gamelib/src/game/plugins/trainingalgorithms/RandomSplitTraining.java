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
package game.plugins.trainingalgorithms;

import game.core.Block;
import game.core.Dataset;
import game.core.DatasetTemplate;
import game.core.TrainingAlgorithm;
import game.core.blocks.Graph;

import java.util.HashSet;
import java.util.Set;

import com.ios.errorchecks.RangeCheck;

public class RandomSplitTraining extends TrainingAlgorithm<Graph> {
	
	public double splitPercent = 1.0;
	
	public RandomSplitTraining() {
		addErrorCheck("splitPercent", new RangeCheck(0.001, 1.00));
	}

	@Override
	protected void train(Dataset trainingSet) {
		recursivelyTrainGraph(block.outputBlock, trainingSet, 1.0/blocksToTrain(block));
	}

	private void recursivelyTrainGraph(Block current, Dataset trainingSet, double increase) {
		for (Block parent: current.parents)
			recursivelyTrainGraph(parent, trainingSet, increase);

		if (!current.trained) {
			executeAnotherTaskAndWait(getProgress()+increase, current.trainingAlgorithm, trainingSet.getRandomSubset(splitPercent));
		}
	}
	
	private int blocksToTrain(Graph graph) {
		Set<Block> blocks = new HashSet<>();
		countBlocks(graph.outputBlock, blocks);
		int count = 0;
		for(Block block: blocks)
			if (!block.trained) count++;
		return count;
	}
	
	private void countBlocks(Block current, Set<Block> blocks) {
		if (!blocks.contains(current)) {
			blocks.add(current);
			for(Block parent: current.parents)
				countBlocks(parent, blocks);
		}
	}

	@Override
	protected String getTrainingPropertyNames() {
		return "";
	}

	@Override
	protected boolean isCompatible(DatasetTemplate datasetTemplate) {
		return true;
	}

}
