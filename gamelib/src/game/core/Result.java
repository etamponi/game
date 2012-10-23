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

import game.configuration.Configurable;

public class Result extends Configurable {
	
	public Experiment experiment;
	
	@Override
	public String toString() {
		if (experiment == null)
			return "empty " + getClass().getSimpleName();
		else
			return experiment.name;
	}

}
