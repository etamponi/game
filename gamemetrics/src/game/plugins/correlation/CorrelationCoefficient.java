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
	
	public void evaluateEverything(SampleIterator it, int samples) {
		computeInputCorrelationMatrix(it, samples);
		computeIOCorrelationMatrix(it, samples);
		computeSyntheticValues(it, samples);
	}
	
	public CorrelationSummary getSummary() {
		return summary;
	}
	
	public abstract void computeInputCorrelationMatrix(SampleIterator it, int samples);
	
	public abstract void computeIOCorrelationMatrix(SampleIterator it, int samples);
	
	public abstract void computeSyntheticValues(SampleIterator it, int samples);

}