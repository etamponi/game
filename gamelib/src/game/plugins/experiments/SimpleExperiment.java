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
package game.plugins.experiments;

import game.core.DatasetBuilder;
import game.core.blocks.Graph;
import game.core.experiments.FullExperiment;
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
	protected void runExperiment(String outputDirectory) {
		Graph graphClone = graph.cloneConfiguration(graph.name + "_trained");
		updateStatus(0.01, "training graph...");
		startAnotherTaskAndWait(0.50, graphClone, trainingDataset.buildDataset());
		updateStatus(0.71, "training complete, testing phase...");
		testedDatasets.add(classifyDataset(0.90, graphClone, testingDataset.buildDataset(), outputDirectory, "tested"));
		trainedGraphs.add(graphClone);
		updateStatus(1.00, "experiment completed");
	}

	@Override
	public String getTaskDescription() {
		return "simple experiment " + this;
	}

}
