package game.core;

import static org.junit.Assert.*;

import game.core.LongTask.LongTaskUpdate;

import java.util.Observable;
import java.util.Observer;

import org.junit.Test;

public class LongTaskTest {
	
	private static class LongTaskImplB extends LongTask {

		@Override
		protected void execute(Object... params) {
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
		}
		
	}
	
	private static class LongTaskImplA extends LongTask {

		@Override
		protected void execute(Object... params) {
			try {
				Thread.sleep(100);
				updateStatus(0.1, "slept for 1 seconds");
				Thread.sleep(100);
				updateStatus(0.2, "slept for 2 seconds");
				Thread.sleep(100);
				updateStatus(0.3, "slept for 3 seconds");
				LongTask other = new LongTaskImplB();
				other.setOption("name", "OtherTask");
				startAnotherTaskAndWait(0.8, other);
				Thread.sleep(100);
				updateStatus(1.0, "slept for a lot of seconds");
			} catch (InterruptedException e) {}
		}
		
	}

	@Test
	public void test() {
		LongTask task = new LongTaskImplA();
		task.addObserver(new Observer() {
			private int count = 0;
			@Override
			public void update(Observable o, Object m) {
				if (m instanceof LongTaskUpdate) {
					LongTask observed = (LongTask)o;
					System.out.println(String.format("%6.2f%% of %s: %s", observed.getPercentCompleted()*100, observed, observed.getCurrentMessage()));
					if (count == 0)
						assertEquals("start task", observed.getCurrentMessage());
					else if (count > 0 && count < 4)
						assertEquals("slept for " + count + " seconds", observed.getCurrentMessage());
					else if (count == 4)
						assertEquals("  0,00% of OtherTask: start task", observed.getCurrentMessage());
					else if (count > 4 && count < 10)
						assertEquals(String.format("%6.2f%%", 20.0*(count-4))+" of OtherTask: LongTaskImplB slept for " + (count-4) + " seconds", observed.getCurrentMessage());
					else if (count == 10)
						assertEquals("100,00% of OtherTask: task finished", observed.getCurrentMessage());
					else if (count == 11)
						assertEquals("slept for a lot of seconds", observed.getCurrentMessage());
					else
						assertEquals("task finished", observed.getCurrentMessage());
					count++;
				}
			}
		});
		task.startTask();
	}

}
