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
import game.core.blocks.Classifier;
import game.core.experiments.ClassificationExperiment;
import game.core.experiments.ClassificationResult;

import java.util.List;

import com.ios.errorchecks.RangeCheck;
import com.ios.errorchecks.RangeCheck.Bound;

public class KFoldCrossValidation extends ClassificationExperiment {
	
	public int folds = 10;
	
	public KFoldCrossValidation() {
		addErrorCheck(new RangeCheck("folds", 2, Bound.LOWER));
	}

	@Override
	protected ResultList runExperiment() {
		Dataset dataset = datasetBuilder.buildDataset();
		ResultList<ClassificationResult> ret = new ResultList<>();
		
		List<Dataset> testings = dataset.getFolds(folds);
		List<Dataset> trainings = dataset.getComplementaryFolds(testings);
		
		for(int i = 0; i < folds; i++) {
			ClassificationResult result = new ClassificationResult();
			Classifier clsClone = classifier.copy();
			updateStatus(getOverallStatus(0.01, i), "training classifier for fold " + (i+1) + "/" + folds);
			executeAnotherTaskAndWait(getOverallStatus(0.70, i), clsClone.trainingAlgorithm, trainings.get(i));
			updateStatus(getOverallStatus(0.70, i), "training complete, testing phase...");
			result.classifiedDataset = classifyDataset(getOverallStatus(0.99, i), clsClone, testings.get(i));
			result.trainedClassifier = clsClone;
			ret.results.add(result);
			updateStatus(getOverallStatus(1.00, i), "finished fold " + (i+1) + "/" + folds);
		}
		updateStatus(1.00, "experiment completed");
		return ret;
	}
	
	private double getOverallStatus(double foldStatus, int fold) {
		return (foldStatus + fold) / folds;
	}

}
