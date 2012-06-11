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

import java.util.List;

public class NoNullElementsCheck implements ErrorCheck<List> {

	@Override
	public String getError(List value) {
		if (value.contains(null))
			return "this list cannot contain null elements";
		else
			return null;
	}

}
