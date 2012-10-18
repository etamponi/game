package game.plugins.pipes.functions;

import game.plugins.pipes.FeatureCombination.CombinationFunction;

import org.apache.commons.math3.linear.RealVector;

public class Add extends CombinationFunction {

	@Override
	public double evaluate(RealVector values) {
		double ret = 0;
		for(double v: values.toArray()) {
			ret += v;
		}
		return ret;
	}

}
