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
	
	public Experiment startExperiment(String prefixDirectory) {
		return startTask(prefixDirectory);
	}
	
	protected abstract void runExperiment(String outputDirectory);

	@Override
	protected Object execute(Object... params) {
		if (completed)
			return this;
		
		String outputDirectory = params[0] + "/" + name;
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
		clone.runExperiment(outputDirectory);
		clone.deleteObserver(o);
		clone.completed = true;
		clone.saveConfiguration(outputDirectory + "/completed_"+name+".config.xml");
		return clone;
	}

}
