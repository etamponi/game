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
import game.core.blocks.Encoder;
import game.plugins.Constraint;

public class CompatibleEncoderConstraint implements Constraint<Encoder> {
	
	private Configurable owner;
	private String templateOption;
	
	public CompatibleEncoderConstraint(Configurable owner, String templateOption) {
		this.owner = owner;
		this.templateOption = templateOption;
	}

	@Override
	public boolean isValid(Encoder o) {
		if (owner.getOption(templateOption) != null)
			return o.getBaseTemplateClass().isAssignableFrom(owner.getOption(templateOption).getClass());
		else
			return false;
	}

}