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
package game.core;

import com.ios.IObject;

public class Result extends IObject {
	
	public Experiment experiment;
	
	@Override
	public String toString() {
		if (experiment == null)
			return "empty " + getClass().getSimpleName();
		else
			return experiment.name;
	}

}
