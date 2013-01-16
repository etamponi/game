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
import game.core.ResultList;
import game.core.blocks.Graph;
import game.core.experiments.ClassificationExperiment;
import game.core.experiments.ClassificationResult;

public class SimpleExperiment extends ClassificationExperiment {
	
	public boolean shuffle = false;
	
	public double testingPercent = 0.30;
	
	@Override
	protected ResultList runExperiment(String outputDirectory) {
		ClassificationResult result = new ClassificationResult();
		Graph graphClone = graph.copy();
		
		Dataset dataset = datasetBuilder.buildDataset();
		Dataset trainset = dataset.getFirsts(1-testingPercent);
		Dataset testset = dataset.getLasts(testingPercent);
		
		updateStatus(0.01, "training graph...");
		executeAnotherTaskAndWait(0.50, graphClone.trainingAlgorithm, trainset);
		updateStatus(0.71, "training complete, testing phase...");
		result.classifiedDataset = classifyDataset(0.90, graphClone, testset);
		result.trainedGraph = graphClone;
		updateStatus(1.00, "experiment completed");
		
		ResultList<ClassificationResult> ret = new ResultList<>();
		ret.results.add(result);
		
		return ret;
	}

}
