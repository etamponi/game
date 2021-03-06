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
package game.core.blocks;

import game.configuration.errorchecks.CompatibilityCheck;
import game.core.Block;
import game.core.InstanceTemplate;
import game.plugins.constraints.Compatible;
import game.plugins.constraints.CompatibleWith;

public abstract class Transducer extends Block implements Compatible<InstanceTemplate> {
	
	public InstanceTemplate template;
	
	public Encoder outputEncoder;
	
	public Transducer() {
		setOptionBinding("template.outputTemplate", "outputEncoder.template");
		setOptionConstraints("outputEncoder", new CompatibleWith(this, "template.outputTemplate"));
		
		setOptionChecks("template", new CompatibilityCheck(this));
	}

	@Override
	public boolean acceptsParents() {
		return true;
	}

	@Override
	public int getFeatureNumber() {
		return outputEncoder.getFeatureNumber();
	}

}
