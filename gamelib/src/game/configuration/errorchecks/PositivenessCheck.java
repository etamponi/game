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

import game.configuration.ErrorCheck;

public class PositivenessCheck implements ErrorCheck<Number> {
	
	private boolean zeroAccepted;
	
	public PositivenessCheck(boolean zeroAccepted) {
		this.zeroAccepted = zeroAccepted;
	}

	@Override
	public String getError(Number value) {
		if (value.doubleValue() < 0 || (!zeroAccepted && value.doubleValue() == 0))
			return "should be positive" + (zeroAccepted ? " or zero" : "");
		else
			return null;
	}

}
