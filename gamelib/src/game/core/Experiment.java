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

import java.util.Observable;
import java.util.Observer;


public abstract class Experiment extends LongTask {
	
	public String name;
	
	public InstanceTemplate template;
	
	public boolean completed = false;

	public Experiment() {
		setPrivateOptions("completed");
	}
	
	public Experiment startExperiment() {
		return startTask();
	}
	
	protected abstract void runExperiment();

	@Override
	protected Object execute(Object... params) {
		if (completed)
			return this;
		final Experiment clone = cloneConfiguration();
		Observer o = new Observer() {
			@Override
			public void update(Observable o, Object arg) {
				if (arg instanceof LongTaskUpdate) {
					updateStatus(clone.getCurrentPercent(), clone.getCurrentMessage());
				}
			}
		};
		clone.name = name;
		clone.addObserver(o);
		clone.runExperiment();
		clone.setOption("completed", true);
		clone.deleteObserver(o);
		return clone;
	}

}
