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
package game.plugins.experiments;

import game.configuration.ConfigurableList;
import game.core.Result;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class CorrelationResult extends Result {

	public ConfigurableList inputCorrelationMatrices = new ConfigurableList(this, RealMatrix.class);
	
	public ConfigurableList ioCorrelationMatrices = new ConfigurableList(this, RealMatrix.class);
	
	public ConfigurableList syntheticValueVectors = new ConfigurableList(this, RealVector.class);

}
