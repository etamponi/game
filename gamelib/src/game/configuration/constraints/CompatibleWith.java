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
package game.configuration.constraints;

import game.configuration.Compatible;
import game.configuration.Constraint;
import game.configuration.Property;

public class CompatibleWith implements Constraint<Compatible> {
	
	private final Property property;

	public CompatibleWith(Property property) {
		this.property = property;
	}

	@Override
	public boolean isValid(Compatible o) {
		Object obj = property.getContent();
		if (obj != null)
			return o.isCompatible(obj);
		else
			return false;
	}

}
