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

import game.configuration.ConfigurableList;
import game.configuration.errorchecks.SizeCheck;
import game.plugins.constraints.CompatibleWith;
import game.utils.Msg;

import java.util.Observable;
import java.util.Observer;


public abstract class Experiment extends LongTask {
	
	public static class ExperimentConstrainedList extends ConfigurableList {
		
		public Experiment constraint;
		
		public ExperimentConstrainedList() {
			// DO NOT NEVER EVER USE THIS!
			
			setOptionBinding("constraint", "*.experiment");
			setOptionConstraint("*", new CompatibleWith(this, "constraint"));
		}
		
		public ExperimentConstrainedList(Experiment e, Class content) {
			super(e, content);
			constraint = e;

			setOptionBinding("constraint", "*.experiment");
			setOptionConstraint("*", new CompatibleWith(this, "constraint"));
		}
		
	}
	
	public static final String TASKNAME = "experiment";
	
	public InstanceTemplate template;
	
	public ExperimentConstrainedList evaluations = new ExperimentConstrainedList(this, Evaluation.class);
	
	public Experiment() {
		setOptionChecks("evaluations", new SizeCheck(1));
	}
	
	public Experiment startExperiment() {
		return startTask(TASKNAME);
	}
	
	protected abstract void runExperiment();

	@Override
	protected Object execute(Object... params) {
		if (getTaskType().equals(TASKNAME)) {
			final Experiment clone = cloneConfiguration();
			Observer o = new Observer() {
				@Override
				public void update(Observable o, Object arg) {
					if (arg instanceof LongTaskUpdate) {
						Msg.info("%6.2f%%: %s", clone.getCurrentPercent()*100, clone.getCurrentMessage());
						updateStatus(clone.getCurrentPercent(), clone.getCurrentMessage());
					}
				}
			};
			clone.name = name;
			clone.addObserver(o);
			clone.runExperiment();
			clone.deleteObserver(o);
			return clone;
		} else {
			return null;
		}
	}

}
