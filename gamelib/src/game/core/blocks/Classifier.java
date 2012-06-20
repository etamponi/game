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
package game.core.blocks;

import game.configuration.errorchecks.CompatibilityCheck;
import game.configuration.errorchecks.SizeCheck;
import game.core.Block;
import game.core.InstanceTemplate;
import game.plugins.constraints.Compatible;
import game.plugins.constraints.CompatibleWith;

public abstract class Classifier extends Block implements Compatible<InstanceTemplate> {
	
	public InstanceTemplate template;
	
	public Encoder outputEncoder;
	
	public Classifier() {
		addOptionBinding("template.outputTemplate", "outputEncoder.template");
		addOptionChecks("parents", new SizeCheck(1));
		
		addOptionChecks("template", new CompatibilityCheck(this));
		
		setOptionConstraint("outputEncoder", new CompatibleWith(this, "template.outputTemplate"));
	}

	@Override
	public boolean acceptsNewParents() {
		return !isTrained();
	}

}
