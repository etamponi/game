/*******************************************************************************
 * Copyright (c) 2012 Emanuele Tamponi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Emanuele Tamponi - initial API and implementation
 ******************************************************************************/
package game.configuration.errorchecks;

import game.configuration.ErrorCheck;

public class RangeCheck implements ErrorCheck<Number> {
	
	public static final int LOWER = 0;
	public static final int UPPER = 1;
	
	private double lowerBound = Double.MIN_VALUE;
	private double upperBound = Double.MAX_VALUE;
	
	public RangeCheck(double lowerBound, double upperBound) {
		assert(upperBound >= lowerBound);
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}
	
	public RangeCheck(int type, double bound) {
		if (type == LOWER)
			this.lowerBound = bound;
		if (type == UPPER)
			this.upperBound = bound;
	}

	@Override
	public String getError(Number value) {
		if (value.doubleValue() < lowerBound)
			return "lower bound is " + lowerBound;
		if (value.doubleValue() > upperBound)
			return "upper bound is " + upperBound;
		return null;
	}

}
