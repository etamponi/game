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
package game.core.experiments;

import game.core.Experiment;
import game.core.blocks.Encoder;
import game.plugins.constraints.CompatibleWith;

public abstract class MetricsExperiment extends Experiment {
	
	public Encoder inputEncoder;
	
	public MetricsExperiment() {
		setOptionBinding("template.inputTemplate", "inputEncoder.template");
		
		setOptionConstraint("inputEncoder", new CompatibleWith(template, "inputTemplate"));
	}

}
