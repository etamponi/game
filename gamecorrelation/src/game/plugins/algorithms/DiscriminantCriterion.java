package game.plugins.algorithms;

import game.plugins.classifiers.Criterion;

import org.apache.commons.math3.linear.RealVector;

public class DiscriminantCriterion extends Criterion {
	
	private RealVector transform;
	
	private double threshold;
	
	public DiscriminantCriterion(RealVector transform, double threshold) {
		super();
		this.transform = transform;
		this.threshold = threshold;
	}

	@Override
	public int decide(RealVector input) {
		if (transform.dotProduct(input) <= threshold)
			return 0;
		else
			return 1;
	}
	
}
