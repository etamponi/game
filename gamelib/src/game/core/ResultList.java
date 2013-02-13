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

import com.ios.IList;
import com.ios.IObject;
import com.ios.triggers.MasterSlaveTrigger;

public class ResultList<R extends Result> extends IObject {
	
	public Experiment experiment;
	
	public IList<R> results;
	
	public ResultList() {
		setContent("results", new IList<>(Result.class));
		addTrigger(new MasterSlaveTrigger(this, "experiment", "results.*.experiment"));
	}
	
	@Override
	public String toString() {
		if (experiment == null)
			return "empty " + getClass().getSimpleName();
		else
			return experiment.toString();
	}

}
