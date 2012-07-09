package game.core.metrics;

import game.core.Dataset;
import game.core.Experiment;
import game.core.Metric;
import game.core.experiments.FullExperiment;

public abstract class FullMetric extends Metric<Dataset> {

	@Override
	public boolean isCompatible(Experiment exp) {
		return exp instanceof FullExperiment;
	}
	
	protected Dataset mergeFolds(Dataset[] folds) {
		Dataset ret = new Dataset();
		for (Dataset fold: folds)
			ret.addAll(fold);
		return ret;
	}
	
}
