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

public abstract class Tester extends LongTask {
	
	private static final String TESTING = "testing";
	
	public Object startTest() {
		return startTask(TESTING);
	}
	
	protected abstract Object test();

	@Override
	protected Object execute(Object... params) {
		if (getTaskType().equals(TESTING))
			return test();
		else
			return null;
	}

}
