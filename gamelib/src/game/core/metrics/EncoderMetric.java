package game.core.metrics;

import game.core.Dataset.EncodedSamples;
import game.core.Experiment;
import game.core.Metric;
import game.core.experiments.EncoderExperiment;

public abstract class EncoderMetric extends Metric<EncodedSamples> {

	@Override
	public boolean isCompatible(Experiment exp) {
		return exp instanceof EncoderExperiment;
	}

}
