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
package testgame.tests;

import static org.junit.Assert.*;

import game.core.LongTask;
import game.core.LongTask.LongTaskUpdate;

import java.util.Observable;
import java.util.Observer;

import org.junit.Test;

public class LongTaskTest {
	
	private static class LongTaskImplB extends LongTask<Void, Void> {

		@Override
		public Void execute(Void empty) {
			try {
				Thread.sleep(100);
				updateStatus(0.2, "LongTaskImplB slept for 1 seconds");
				Thread.sleep(100);
				updateStatus(0.4, "LongTaskImplB slept for 2 seconds");
				Thread.sleep(100);
				updateStatus(0.6, "LongTaskImplB slept for 3 seconds");
				Thread.sleep(100);
				updateStatus(0.8, "LongTaskImplB slept for 4 seconds");
				Thread.sleep(100);
				updateStatus(1.0, "LongTaskImplB slept for 5 seconds");
			} catch (InterruptedException e) {}
			return null;
		}

		@Override
		public String getTaskDescription() {
			return "test task B";
		}
		
	}
	
	private static class LongTaskImplA extends LongTask<Void, Void> {

		@Override
		public Void execute(Void empty) {
			try {
				updateStatus(0.0, "start task " + getTaskDescription());
				Thread.sleep(100);
				updateStatus(0.1, "slept for 1 seconds");
				Thread.sleep(100);
				updateStatus(0.2, "slept for 2 seconds");
				Thread.sleep(100);
				updateStatus(0.3, "slept for 3 seconds");
				LongTask other = new LongTaskImplB();
				other.setOption("name", "OtherTask");
				executeAnotherTaskAndWait(0.8, other, null);
				Thread.sleep(100);
				updateStatus(1.0, "slept for a lot of seconds");
				updateStatus(1.0, "task finished (" + getTaskDescription() + ")");
			} catch (InterruptedException e) {}
			return null;
		}

		@Override
		public String getTaskDescription() {
			return "test task A";
		}
		
	}

	@Test
	public void test() {
		LongTaskImplA task = new LongTaskImplA();
		task.addObserver(new Observer() {
			private int count = 0;
			@Override
			public void update(Observable o, Object m) {
				if (m instanceof LongTaskUpdate) {
					LongTask observed = (LongTask)o;
					System.out.println(String.format("%5.1f%% of %s: %s", observed.getCurrentPercent()*100, observed, observed.getCurrentMessage()));
					if (count == 0)
						assertEquals("start task test task A", observed.getCurrentMessage());
					else if (count > 0 && count < 4)
						assertEquals("slept for " + count + " seconds", observed.getCurrentMessage());
					else if (count > 3 && count < 9)
						assertEquals("LongTaskImplB slept for " + (count-3) + " seconds", observed.getCurrentMessage());
					else if (count == 9)
						assertEquals("slept for a lot of seconds", observed.getCurrentMessage());
					else
						assertEquals("task finished (test task A)", observed.getCurrentMessage());
					count++;
				}
			}
		});
		task.execute(null);
	}

}
