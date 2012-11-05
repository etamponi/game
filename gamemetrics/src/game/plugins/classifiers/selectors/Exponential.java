package game.plugins.classifiers.selectors;

import game.plugins.classifiers.FeatureSelector.Transform;

public class Exponential extends Transform {
	
	public double tau = 1;

	@Override
	public double value(double x, double p) {
		return x * Math.exp(- p / tau);
	}

}
