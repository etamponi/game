package game.core;

import java.util.Observable;
import java.util.Observer;

import game.configuration.Configurable;

public abstract class LongTask extends Configurable {
	
	public class LongTaskUpdate {}

	private double percentCompleted;
	private String currentMessage;
	
	protected abstract void execute(Object... params);
	
	public void startTask(Object... params) {
		updateStatus(0.0, "start task");
		execute(params);
		updateStatus(1.0, "task finished");
	}
	
	public void startAnotherTaskAndWait(double percentAtEnd, LongTask task, Object... params) {
		final double percentAtStart = percentCompleted;
		final double ratio = percentAtEnd - percentCompleted;
		Observer temp = new Observer() {
			@Override
			public void update(Observable o, Object m) {
				if (m instanceof LongTaskUpdate) {
					LongTask task = (LongTask)o;
					LongTask.this.updateStatus(percentAtStart + task.getPercentCompleted()*ratio,
						String.format("%6.2f%% of %s: %s", task.getPercentCompleted()*100, task, task.getCurrentMessage()));
				}
			}
		};
		task.addObserver(temp);
		task.startTask(params);
		task.deleteObserver(temp);
	}
	
	public double getPercentCompleted() {
		return percentCompleted;
	}
	
	public String getCurrentMessage() {
		return currentMessage;
	}
	
	protected void updateStatus(double percentCompleted, String message) {
		this.percentCompleted = percentCompleted;
		this.currentMessage = message;
		setChanged();
		notifyObservers(new LongTaskUpdate());
	}
	
}
