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

import java.util.Observable;
import java.util.Observer;

import game.configuration.Configurable;

public abstract class LongTask<R, P> extends Configurable {
	
	public static class LongTaskUpdate {}

	private double currentPercent;
	private String currentMessage;

	public abstract R execute(P param);
	
	protected <RR, PP> RR executeAnotherTaskAndWait(double percentAtEnd, LongTask<RR, PP> task, PP param) {
		final double percentAtStart = currentPercent;
		final double ratio = percentAtEnd - percentAtStart;
		Observer temp = new Observer() {
			@Override
			public void update(Observable o, Object m) {
				if (m instanceof LongTaskUpdate) {
					LongTask task = (LongTask)o;
					LongTask.this.updateStatus(percentAtStart + task.getCurrentPercent()*ratio, task.getCurrentMessage());
				}
			}
		};
		task.addObserver(temp);
		RR ret = task.execute(param);
		task.deleteObserver(temp);
		return ret;
	}
	
	public double getCurrentPercent() {
		return currentPercent;
	}
	
	public String getCurrentMessage() {
		return currentMessage;
	}
	
	protected void updateStatus(double percentCompleted, String message) {
		this.currentPercent = percentCompleted;
		this.currentMessage = message;
		setChanged();
		notifyObservers(new LongTaskUpdate());
	}
	
}
