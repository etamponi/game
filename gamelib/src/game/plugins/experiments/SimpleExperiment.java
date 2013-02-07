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

import com.ios.ErrorCheck;

import game.core.Dataset;
import game.core.DatasetBuilder;
import game.core.DatasetTemplate;
import game.core.ResultList;
import game.core.blocks.Graph;
import game.core.experiments.ClassificationExperiment;
import game.core.experiments.ClassificationResult;

public class SimpleExperiment extends ClassificationExperiment {
	
//	public boolean wholeTraining = false;
	
//	public double testingPercent = 0.30;
	
	public DatasetBuilder testingDataset;
	
	public SimpleExperiment() {
		addErrorCheck("testingDataset.datasetTemplate", new ErrorCheck<DatasetTemplate>() {
			private SimpleExperiment self = SimpleExperiment.this;
			@Override
			public String getError(DatasetTemplate value) {
				if (value.equals(self.datasetBuilder.datasetTemplate))
					return null;
				else
					return "must be the same as the training dataset template";
			}
		});
	}
	
	@Override
	protected ResultList runExperiment() {
		ClassificationResult result = new ClassificationResult();
		Graph graphClone = graph.copy();
		
		/*
		Dataset dataset = datasetBuilder.buildDataset();
		Collections.shuffle(dataset, getRandom());
		Dataset trainset = wholeTraining ? dataset : dataset.getFirsts(1 - testingPercent);
		Dataset testset = wholeTraining ? dataset.getRandomSubset(testingPercent) : dataset.getLasts(testingPercent);
		*/
		
		Dataset trainset = datasetBuilder.buildDataset();
		Dataset testset = testingDataset.buildDataset();
		
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
