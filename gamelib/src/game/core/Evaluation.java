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
import game.configuration.errorchecks.CompatibilityCheck;
import game.plugins.constraints.Compatible;

public abstract class Evaluation extends Configurable implements Compatible<Experiment> {
	
	public Experiment experiment;
	
	public Evaluation() {
		name = getClass().getSimpleName();
		setOptionChecks("template", new CompatibilityCheck(this));
	}
	
	public abstract boolean isReady();
	
	public abstract void evaluate(Dataset... folds);
	
	public abstract String prettyPrint();
	
}
