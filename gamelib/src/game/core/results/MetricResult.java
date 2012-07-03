package game.core.results;

import game.core.EncodedSample;
import game.core.Experiment;
import game.core.Result;
import game.core.experiments.MetricsExperiment;

import java.util.List;

public abstract class MetricResult extends Result<List<EncodedSample>> {

	@Override
	public boolean isCompatible(Experiment exp) {
		return exp instanceof MetricsExperiment;
	}

}
