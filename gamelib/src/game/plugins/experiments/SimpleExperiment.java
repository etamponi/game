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
import game.core.DatasetTemplate;
import game.core.ResultList;
import game.core.blocks.Classifier;
import game.core.experiments.ClassificationExperiment;
import game.core.experiments.ClassificationResult;

import com.ios.ErrorCheck;

public class SimpleExperiment extends ClassificationExperiment {
	
	public DatasetBuilder testingDataset;
	
	public SimpleExperiment() {
		addErrorCheck(new ErrorCheck<SimpleExperiment>() {
			@Override public String getError() {
				DatasetTemplate testingTpl = getRoot().getContent("testingDataset.datasetTemplate");
				if (testingTpl == null)
					return "testing datasetTemplate is null";
				
				DatasetTemplate trainingTpl = getRoot().getContent("datasetBuilder.datasetTemplate");
				if (trainingTpl == null)
					return null;
				
				if (testingTpl.equals(trainingTpl))
					return null;
				else
					return "testing and training datasetTemplate must be the equal";
			}
		});
	}
	
	@Override
	protected ResultList runExperiment() {
		ClassificationResult result = new ClassificationResult();
		Classifier clsClone = classifier.copy();
		
		Dataset trainset = datasetBuilder.buildDataset();
		Dataset testset = testingDataset.buildDataset();
		
		updateStatus(0.01, "training graph...");
		executeAnotherTaskAndWait(0.50, clsClone.trainingAlgorithm, trainset);
		updateStatus(0.71, "training complete, testing phase...");
		result.classifiedDataset = classifyDataset(0.90, clsClone, testset);
		result.trainedClassifier = clsClone;
		updateStatus(1.00, "experiment completed");
		
		ResultList<ClassificationResult> ret = new ResultList<>();
		ret.results.add(result);
		
		return ret;
	}

}
