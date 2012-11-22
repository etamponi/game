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
package game.editorsystem.constraints;

import com.ios.Constraint;

import game.editorsystem.PropertyEditor;

public class CanEditConstraint implements Constraint<PropertyEditor> {
	
	private Class type;
	
	public CanEditConstraint(Class type) {
		this.type = type;
	}

	@Override
	public boolean isValid(PropertyEditor o) {
		return o.canEdit(type);
	}

}
