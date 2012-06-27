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

import game.configuration.errorchecks.SizeCheck;
import game.utils.Msg;

import java.util.Observable;
import java.util.Observer;


public abstract class Experiment extends LongTask {
	
	public static final String TASKNAME = "experiment";
	
	public InstanceTemplate template;
	
	public boolean resultsReady = false;
	
	public TemplateConstrainedList evaluations = new TemplateConstrainedList(this, Evaluation.class);
	
	public Experiment() {
		addObserver(new Observer() {
			@Override
			public void update(Observable observedOption, Object message) {
				if (message instanceof LongTaskUpdate) {
					Msg.info("%6.2f%%: %s", getCurrentPercent()*100, getCurrentMessage());
				}
			}
		});
		
		setOptionBinding("template", "evaluations.constraint");
		
		setOptionChecks("evaluations", new SizeCheck(1));
		
		setInternalOptions("resultsReady");
	}
	
	public void startExperiment() {
		startTask(TASKNAME);
	}
	
	protected abstract void runExperiment();

	@Override
	protected Object execute(Object... params) {
		if (getTaskType().equals(TASKNAME)) {
			runExperiment();
			resultsReady = true;
		}
		return null;
	}

}
