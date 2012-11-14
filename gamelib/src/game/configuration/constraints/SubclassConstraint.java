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

import game.configuration.Constraint;

public class SubclassConstraint implements Constraint<Object> {
	
	private Class[] baseClasses;

	public SubclassConstraint(Class... baseClasses) {
		assert(baseClasses.length > 0);
		this.baseClasses = baseClasses;
	}

	@Override
	public boolean isValid(Object o) {
		for(Class base: baseClasses)
			if (base.isAssignableFrom(o.getClass())) return true;
		return false;
	}

}
