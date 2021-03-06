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
package game.plugins.constraints;

import game.configuration.Configurable;
import game.plugins.Constraint;

public class CompatibleWith implements Constraint<Compatible> {
	
	private static final String THIS = "this";
	
	private Configurable owner;
	private String constraintOption;

	public CompatibleWith(Configurable owner) {
		this.owner = owner;
		this.constraintOption = THIS;
	}
	
	public CompatibleWith(Configurable owner, String constraintOption) {
		this.owner = owner;
		this.constraintOption = constraintOption;
	}

	@Override
	public boolean isValid(Compatible o) {
		if (constraintOption.equals(THIS))
			return o.isCompatible(owner);
		else if (owner.getOption(constraintOption) != null)
			return o.isCompatible(owner.getOption(constraintOption));
		else
			return false;
	}

}
