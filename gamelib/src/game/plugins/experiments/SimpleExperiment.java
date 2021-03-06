/*******************************************************************************
 * Copyright (c) 2012 Emanuele.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Emanuele - initial API and implementation
 ******************************************************************************/
package game.plugins.experiments;

import game.core.DatasetBuilder;
import game.core.blocks.PredictionGraph;
import game.core.experiments.FullExperiment;
import game.core.experiments.FullResult;
import game.plugins.constraints.CompatibleWith;

public class SimpleExperiment extends FullExperiment {

	public DatasetBuilder trainingDataset;
	
	public DatasetBuilder testingDataset;
		
	public SimpleExperiment() {
		setOptionBinding("template", "trainingDataset.template",
									 "testingDataset.template");
		setOptionConstraints("trainingDataset", new CompatibleWith(this, "template"));
		setOptionConstraints("testingDataset", new CompatibleWith(this, "template"));
	}

	@Override
	protected FullResult runExperiment(String outputDirectory) {
		FullResult ret = new FullResult();
		PredictionGraph graphClone = graph.cloneConfiguration(graph.name + "_0");
		updateStatus(0.01, "training graph...");
		executeAnotherTaskAndWait(0.50, graphClone.trainingAlgorithm, trainingDataset.buildDataset());
		updateStatus(0.71, "training complete, testing phase...");
		ret.testedDatasets.add(classifyDataset(0.90, graphClone, testingDataset.buildDataset(), outputDirectory+"/dataset_tested"));
		ret.trainedGraphs.add(graphClone);
		updateStatus(1.00, "experiment completed");
		return ret;
	}

}
