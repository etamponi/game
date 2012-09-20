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

import game.configuration.errorchecks.RangeCheck;
import game.core.Dataset;
import game.core.DatasetBuilder;
import game.core.blocks.Graph;
import game.core.experiments.FullExperiment;
import game.plugins.constraints.CompatibleWith;

import java.util.List;

public class KFoldCrossValidation extends FullExperiment {
	
	public int folds = 10;
	
	public DatasetBuilder dataset;
	
	public KFoldCrossValidation() {
		setOptionBinding("template", "dataset.template");
		setOptionConstraints("dataset", new CompatibleWith(this, "template"));
		
		setOptionChecks("folds", new RangeCheck(RangeCheck.LOWER, 2));
		
		setPrivateOptions("trainedGraphs");
	}

	@Override
	protected void runExperiment(String outputDirectory) {
		Dataset complete = dataset.buildDataset();
		
		List<Dataset> testings = complete.getFolds(folds);
		List<Dataset> trainings = complete.getComplementaryFolds(testings);
		
		for(int i = 0; i < folds; i++) {
			Graph graphClone = graph.cloneConfiguration(graph.name+"_"+i);
			updateStatus(getOverallStatus(0.01, i), "training graph for fold " + (i+1) + "/" + folds);
			startAnotherTaskAndWait(getOverallStatus(0.70, i), graphClone, trainings.get(i));
			updateStatus(getOverallStatus(0.70, i), "training complete, testing phase...");
			testedDatasets.add(classifyDataset(getOverallStatus(0.99, i), graphClone, testings.get(i), outputDirectory, "tested_"+i));
			trainedGraphs.add(graphClone);
			updateStatus(getOverallStatus(1.00, i), "finished fold " + (i+1) + "/" + folds);
		}
		updateStatus(1.00, "experiment completed");
	}
	
	private double getOverallStatus(double foldStatus, int fold) {
		return (foldStatus + fold) / folds;
	}

	@Override
	public String getTaskDescription() {
		return "k-fold cross-validation using " + folds + " folds";
	}

}
