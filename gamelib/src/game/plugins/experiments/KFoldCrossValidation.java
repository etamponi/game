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
import game.core.blocks.PredictionGraph;
import game.core.experiments.FullExperiment;
import game.core.experiments.FullResult;

import java.util.List;

import com.ios.Property;
import com.ios.constraints.CompatibleWith;
import com.ios.errorchecks.RangeCheck;
import com.ios.errorchecks.RangeCheck.Bound;
import com.ios.triggers.MasterSlaveTrigger;

public class KFoldCrossValidation extends FullExperiment {
	
	public int folds = 10;
	
	public DatasetBuilder dataset;
	
	public KFoldCrossValidation() {
		addTrigger(new MasterSlaveTrigger(this, "template", "dataset.template"));
		addConstraint("dataset", new CompatibleWith(new Property(this, "template")));
		
		addErrorCheck("folds", new RangeCheck(2, Bound.LOWER));
	}

	@Override
	protected FullResult runExperiment(String outputDirectory) {
		Dataset complete = dataset.buildDataset();
		FullResult ret = new FullResult();
		
		List<Dataset> testings = complete.getFolds(folds);
		List<Dataset> trainings = complete.getComplementaryFolds(testings);
		
		for(int i = 0; i < folds; i++) {
			PredictionGraph graphClone = graph.copy();
			graphClone.setContent("name", graph.name + "_" + i);
			updateStatus(getOverallStatus(0.01, i), "training graph for fold " + (i+1) + "/" + folds);
			executeAnotherTaskAndWait(getOverallStatus(0.70, i), graphClone.trainingAlgorithm, trainings.get(i));
			updateStatus(getOverallStatus(0.70, i), "training complete, testing phase...");
			ret.testedDatasets.add(classifyDataset(getOverallStatus(0.99, i), graphClone, testings.get(i)));
			ret.trainedGraphs.add(graphClone);
			updateStatus(getOverallStatus(1.00, i), "finished fold " + (i+1) + "/" + folds);
		}
		updateStatus(1.00, "experiment completed");
		return ret;
	}
	
	private double getOverallStatus(double foldStatus, int fold) {
		return (foldStatus + fold) / folds;
	}

}
