/*******************************************************************************
 * Copyright (c) 2012 Emanuele.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Emanuele - initial API and implementation
 ******************************************************************************/
package game.plugins.correlation;

import game.configuration.Configurable;
import game.core.Dataset.SampleIterator;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public abstract class CorrelationCoefficient extends Configurable {
	
	public int maxSamples = 10000;
	
	public abstract RealMatrix computeInputCorrelationMatrix(SampleIterator it);
	
	public abstract RealMatrix computeIOCorrelationMatrix(SampleIterator it);
	
	public abstract RealVector computeSyntheticValues(SampleIterator it);

}
