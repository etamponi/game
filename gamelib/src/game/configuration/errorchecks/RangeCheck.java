package game.configuration.errorchecks;

import game.configuration.ErrorCheck;

public class RangeCheck implements ErrorCheck<Number> {
	
	private Number lowerBound, upperBound;
	
	public enum Bound {
		LOWER, UPPER
	}
	
	public RangeCheck(Number lowerBound, Number upperBound) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}
	
	public RangeCheck(Number bound, Bound type) {
		if (type == Bound.LOWER) {
			this.lowerBound = bound;
			this.upperBound = Double.POSITIVE_INFINITY;
		} else {
			this.upperBound = bound;
			this.lowerBound = Double.NEGATIVE_INFINITY;
		}
	}

	@Override
	public String getError(Number value) {
		if (value.doubleValue() > upperBound.doubleValue())
			return "upper bound is " + upperBound + " (current value: " + value + ")";
		if (value.doubleValue() < lowerBound.doubleValue())
			return "lower bound is " + lowerBound + " (current value: " + value + ")";
		return null;
	}

}
