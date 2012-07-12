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
package game.core.metrics;

import game.core.Experiment;
import game.core.Metric;
import game.core.experiments.EncoderExperiment;

public abstract class EncoderMetric extends Metric<EncoderExperiment> {

	@Override
	public boolean isCompatible(Experiment exp) {
		return exp instanceof EncoderExperiment;
	}

}
