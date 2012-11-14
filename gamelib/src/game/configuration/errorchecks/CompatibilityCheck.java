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
package game.configuration.errorchecks;

import game.configuration.Compatible;
import game.configuration.ErrorCheck;

@SuppressWarnings("rawtypes")
public class CompatibilityCheck implements ErrorCheck {
	
	private Compatible compatible;
	
	public CompatibilityCheck(Compatible compatible) {
		this.compatible = compatible;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getError(Object value) {
		if (!compatible.isCompatible(value))
			return "this value is not compatible with the option";
		else
			return null;
	}

}
