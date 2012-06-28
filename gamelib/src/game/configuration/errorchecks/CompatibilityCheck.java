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
package game.configuration.errorchecks;

import game.configuration.ErrorCheck;
import game.plugins.constraints.Compatible;

public class CompatibilityCheck implements ErrorCheck {
	
	private Compatible owner;
	
	public CompatibilityCheck(Compatible owner) {
		this.owner = owner;
	}

	@Override
	public String getError(Object value) {
		if (!owner.isCompatible(value))
			return "this value is not compatible with the option";
		else
			return null;
	}

}
