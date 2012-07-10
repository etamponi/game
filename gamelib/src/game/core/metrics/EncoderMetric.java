package game.core.metrics;

import game.core.Experiment;
import game.core.Metric;
import game.core.experiments.EncoderExperiment;

public abstract class EncoderMetric extends Metric<EncoderExperiment> {

	@Override
	public boolean isCompatible(Experiment exp) {
		return exp instanceof EncoderExperiment;
	}

}
