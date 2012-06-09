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

import game.plugins.Constraint;

public class TrueConstraint implements Constraint {
	
	static final TrueConstraint instance = new TrueConstraint();
	
	public static TrueConstraint getInstance() {
		return instance;
	}

	@Override
	public boolean isValid(Object o) {
		return true;
	}

}
