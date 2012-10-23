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

import java.util.Collection;

public class SizeCheck implements ErrorCheck<Collection> {
	
	private int minimumSize, maximumSize;

	public SizeCheck(int minimumSize) {
		this.minimumSize = minimumSize;
		this.maximumSize = Integer.MAX_VALUE;
	}
	
	public SizeCheck(int minimumSize, int maximumSize) {
		this.minimumSize = minimumSize;
		this.maximumSize = maximumSize;
	}

	@Override
	public String getError(Collection value) {
		if (value.size() < minimumSize)
			return "should have at least " + minimumSize + " elements";
		else if (value.size() > maximumSize)
			return "should have no more than " + maximumSize + " elements";
		else
			return null;
	}

}
