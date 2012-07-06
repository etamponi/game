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

import game.core.Dataset;
import game.core.DatasetBuilder;
import game.core.Graph;
import game.core.experiments.FullExperiment;
import game.core.results.FullResult;
import game.plugins.constraints.CompatibleWith;
import game.plugins.results.TrainedGraphList;
import game.utils.Msg;

public class SimpleExperiment extends FullExperiment {

	public DatasetBuilder trainingDataset;
	
	public DatasetBuilder testingDataset;
		
	public SimpleExperiment() {
		setOptionBinding("template", "trainingDataset.template",
									 "testingDataset.template");
		
		setOptionConstraint("trainingDataset", new CompatibleWith(this, "template"));
		setOptionConstraint("testingDataset", new CompatibleWith(this, "template"));
	}

	@Override
	protected void runExperiment() {
		Graph graphClone = graph.cloneConfiguration();
		updateStatus(0.01, "start graph training...");
		startAnotherTaskAndWait(0.50, trainer, graphClone, trainingDataset.buildDataset());
		updateStatus(0.51, "training complete, beginning testing phase...");
		Dataset testedDataset = startAnotherTaskAndWait(0.90, graphClone, testingDataset.buildDataset());
		updateStatus(0.91, "testing complete, beginning evaluation phase...");
		for(FullResult result: results.getList(FullResult.class)) {
			result.evaluate(testedDataset);
			Msg.data(result.prettyPrint());
		}
		TrainedGraphList trainedGraph = new TrainedGraphList();
		trainedGraph.graphs.add(graphClone);
		results.add(trainedGraph);
		updateStatus(1.00, "experiment completed.");
	}

	@Override
	public String getTaskDescription() {
		return "simple experiment " + name;
	}

}
