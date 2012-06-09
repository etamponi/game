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
package game.plugins.constraints;

import game.configuration.Configurable;
import game.core.InstanceTemplate;
import game.core.blocks.Classifier;
import game.plugins.Constraint;

public class CompatibleClassifierConstraint implements Constraint<Classifier> {
	
	private Configurable owner;
	private String templateOption;
	
	public CompatibleClassifierConstraint(Configurable owner, String templateOption) {
		this.owner = owner;
		this.templateOption = templateOption;
	}

	@Override
	public boolean isValid(Classifier o) {
		if (owner.getOption(templateOption) != null)
			return o.supportsTemplate((InstanceTemplate)owner.getOption(templateOption));
		else
			return false;
	}

}
