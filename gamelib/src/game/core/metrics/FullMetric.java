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
package game.core.metrics;

import game.core.Metric;
import game.core.Result;
import game.core.experiments.FullResult;

public abstract class FullMetric extends Metric<FullResult> {

	@Override
	public boolean isCompatible(Result result) {
		return result instanceof FullResult;
	}
	
}
