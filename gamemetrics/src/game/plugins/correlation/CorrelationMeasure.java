package game.plugins.correlation;

import game.configuration.Configurable;
import game.core.Dataset.SampleIterator;

import org.apache.commons.math3.linear.RealVector;

public abstract class CorrelationMeasure extends Configurable {
	
	public abstract RealVector evaluate(SampleIterator it, int samples);

}
