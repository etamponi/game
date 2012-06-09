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
import game.core.Decoder;
import game.plugins.Constraint;

public class CompatibleDecoderConstraint implements Constraint<Decoder> {

	private Configurable owner;
	private String encoderOption;
	
	public CompatibleDecoderConstraint(Configurable owner, String encoderOption) {
		this.owner = owner;
		this.encoderOption = encoderOption;
	}

	@Override
	public boolean isValid(Decoder o) {
		if (owner.getOption(encoderOption) != null)
			return o.getBaseEncoderClass().isAssignableFrom(owner.getOption(encoderOption).getClass());
		else
			return false;
	}

}
