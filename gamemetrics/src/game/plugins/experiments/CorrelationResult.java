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
package game.plugins.experiments;

import game.core.Result;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.RealVector;

public class CorrelationResult extends Result {
	
	public List<RealVector> perClassMeasures = new ArrayList<>();
	
	public List<Double> overallMeasures = new ArrayList<>();
	
	public CorrelationResult() {
		setPrivateOptions("perClassMeasures", "overallMeasures");
	}

	public List<RealVector> getPerClassMeasures() {
		return perClassMeasures;
	}

	public List<Double> getOverallMeasures() {
		return overallMeasures;
	}

}
