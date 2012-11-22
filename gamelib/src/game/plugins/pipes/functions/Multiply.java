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
package game.plugins.pipes.functions;

import game.plugins.pipes.FeatureCombination.CombinationFunction;

import org.apache.commons.math3.linear.RealVector;

public class Multiply extends CombinationFunction {
	
	@Override
	public double evaluate(RealVector values) {
		double ret = 1;
		for(double v: values.toArray()) {
			ret *= v;
		}
		return ret;
	}

}
