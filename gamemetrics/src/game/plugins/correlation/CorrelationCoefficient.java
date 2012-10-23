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

public abstract class CorrelationCoefficient extends Configurable {
	
	private CorrelationSummary summary = new CorrelationSummary();
	
	public int samples = 1000;;
	/*
	public void evaluateEverything(SampleIterator it) {
		computeInputCorrelationMatrix(it);
		computeIOCorrelationMatrix(it);
		computeSyntheticValues(it);
	}
	*/
	public CorrelationSummary getSummary() {
		return summary;
	}
	
	public void clear() {
		summary = new CorrelationSummary();
	}
	
	public abstract boolean computeInputCorrelationMatrix(SampleIterator it);
	
	public abstract boolean computeIOCorrelationMatrix(SampleIterator it);
	
	public abstract boolean computeSyntheticValues(SampleIterator it);

}
