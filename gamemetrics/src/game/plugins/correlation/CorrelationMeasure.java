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

import org.apache.commons.math3.linear.RealVector;

public abstract class CorrelationMeasure extends Configurable {
	
	public abstract RealVector evaluate(SampleIterator it, int samples);

}
