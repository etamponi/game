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

public class LengthCheck implements ErrorCheck<String> {
	
	private int minimumLength;
	
	public LengthCheck(int minimumLength) {
		this.minimumLength = minimumLength;
	}

	@Override
	public String getError(String value) {
		if (value.length() < minimumLength)
			return "must have at least " + minimumLength + " characters";
		else
			return null;
	}

}
