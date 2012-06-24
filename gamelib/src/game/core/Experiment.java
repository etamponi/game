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
import java.util.logging.Logger;


public abstract class Experiment extends LongTask {
	
	private static final Logger LOGGER = Logger.getLogger("game.experiment");
	
	public static final String TASKNAME = "experiment";
	
	public static Logger getLogger() {
		return LOGGER;
	}
	
	public Experiment() {
		addObserver(new Observer() {
			@Override
			public void update(Observable observedOption, Object message) {
				if (message instanceof LongTaskUpdate) {
					String msg = String.format("%6.2f%%: %s", getCurrentPercent()*100, getCurrentMessage());
					LOGGER.config(msg);
					System.out.println(msg);
				}
			}
		});
	}
	
	public void startExperiment() {
		startTask(TASKNAME);
	}
	
	protected abstract void runExperiment();

	@Override
	protected Object execute(Object... params) {
		if (getTaskType().equals(TASKNAME))
			runExperiment();
		return null;
	}
	
	public void println(String msg, Object... args) {
		
	}

}
