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

import com.ios.IObject;
import com.ios.Observer;
import com.ios.Property;

public abstract class LongTask<R, P> extends IObject {

	private double progress;
	
	private String message;
	
	public abstract R execute(P param);
	
	protected <RR, PP> RR executeAnotherTaskAndWait(double endingProgress, final LongTask<RR, PP> task, PP param) {
		final double startingProcess = progress;
		final double scale = endingProgress - startingProcess;
		
		Observer obs = new Observer(task) {
			@Override
			protected void action(Property changedPath) {
				if (changedPath.getPath().isEmpty()) {
					LongTask.this.updateStatus(startingProcess + task.getProgress()*scale, getMessage() + ": " + task.getMessage());
				}
			}
		};
		
		RR ret = task.execute(param);
		
		obs.detach();
		
		return ret;
	}
	
	public double getProgress() {
		return progress;
	}

	public String getMessage() {
		return message;
	}

	protected void updateStatus(double progress, String message) {
		this.message = message;
		this.progress = progress;
		
		notifyObservers();
	}
	
}
