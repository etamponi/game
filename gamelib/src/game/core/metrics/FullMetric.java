package game.core.metrics;

import game.core.Experiment;
import game.core.Metric;
import game.core.experiments.FullExperiment;

public abstract class FullMetric extends Metric<FullExperiment> {

	@Override
	public boolean isCompatible(Experiment exp) {
		return exp instanceof FullExperiment;
	}
	
}
