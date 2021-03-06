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

import java.util.List;

public class ListMustContainCheck implements ErrorCheck<List> {
	
	private Object element;

	public ListMustContainCheck(Object element) {
		this.element = element;
	}

	@Override
	public String getError(List value) {
		if (!value.contains(element))
			return "list must contain " + element;
		else
			return null;
	}

}
