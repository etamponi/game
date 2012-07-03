package game.plugins.experiments;

import game.configuration.errorchecks.RangeCheck;
import game.core.Dataset;
import game.core.DatasetBuilder;
import game.core.Graph;
import game.core.experiments.FullExperiment;
import game.core.results.FullResult;
import game.plugins.constraints.CompatibleWith;
import game.utils.Msg;

public class KFoldCrossValidation extends FullExperiment {
	
	public int folds = 10;
	
	public DatasetBuilder dataset;
	
	public KFoldCrossValidation() {
		setOptionBinding("template", "dataset.template");
		
		setOptionChecks("folds", new RangeCheck(RangeCheck.LOWER, 2));
		
		setOptionConstraint("dataset", new CompatibleWith(this, "template"));
	}

	@Override
	protected void runExperiment() {
		Dataset complete = dataset.buildDataset();
		
		Dataset[] testings = complete.getFolds(folds);
		Dataset[] trainings = complete.getFoldComplements(folds);
		
		for(int i = 0; i < folds; i++) {
			Graph graphClone = graph.cloneConfiguration();
			updateStatus(getOverallStatus(0.01, i), "start training for fold " + (i+1) + "/" + folds);
			startAnotherTaskAndWait(getOverallStatus(0.50, i), trainer, graphClone, trainings[i]);
			updateStatus(getOverallStatus(0.51, i), "training complete, beginning testing phase...");
			testings[i] = startAnotherTaskAndWait(getOverallStatus(0.99, i), graphClone, testings[i]);
			updateStatus(getOverallStatus(1.00, i), "finished fold " + (i+1) + "/" + folds);
		}
		updateStatus(0.91, "fold training/testing finished, begin evaluation");
		for(FullResult result: results.getList(FullResult.class)) {
			result.evaluate(testings);
			Msg.data(result.prettyPrint());
		}
		updateStatus(1.00, "experiment completed.");
	}
	
	private double getOverallStatus(double foldStatus, int fold) {
		return 0.90*(foldStatus + fold) / folds;
	}

	@Override
	public String getTaskDescription() {
		return "k-fold cross-validation using " + folds + " folds";
	}

}
