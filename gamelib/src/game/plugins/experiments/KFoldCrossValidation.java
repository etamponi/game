package game.plugins.experiments;

import game.configuration.errorchecks.RangeCheck;
import game.core.Dataset;
import game.core.DatasetBuilder;
import game.core.Graph;
import game.core.experiments.FullExperiment;
import game.plugins.constraints.CompatibleWith;

public class KFoldCrossValidation extends FullExperiment {
	
	public int folds = 10;
	
	public DatasetBuilder dataset;
	
	public KFoldCrossValidation() {
		setOptionBinding("template", "dataset.template");
		
		setOptionChecks("folds", new RangeCheck(RangeCheck.LOWER, 2));
		
		setOptionConstraint("dataset", new CompatibleWith(this, "template"));
		
		setPrivateOptions("trainedGraphs");
	}

	@Override
	protected void runExperiment() {
		Dataset complete = dataset.buildDataset();
		
		Dataset[] testings = complete.getFolds(folds);
		Dataset[] trainings = complete.getFoldComplements(folds);
		
		for(int i = 0; i < folds; i++) {
			Graph graphClone = graph.cloneConfiguration();
			updateStatus(getOverallStatus(0.01, i), "training graph for fold " + (i+1) + "/" + folds);
			startAnotherTaskAndWait(getOverallStatus(0.70, i), trainer, graphClone, trainings[i]);
			updateStatus(getOverallStatus(0.70, i), "training complete, testing phase...");
			testedDatasets.add((Dataset)startAnotherTaskAndWait(getOverallStatus(0.99, i), graphClone, testings[i]));
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
