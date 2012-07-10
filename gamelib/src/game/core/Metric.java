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
package game.core;

import game.configuration.Configurable;
import game.plugins.constraints.Compatible;

public abstract class Metric<E extends Experiment> extends Configurable implements Compatible<Experiment> {
	
	public abstract boolean isReady();
	
	public abstract void evaluate(E e);
	
	public abstract String prettyPrint();
	
}
