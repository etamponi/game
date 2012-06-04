package game.core;

import java.util.Observable;
import java.util.Observer;

import game.configuration.Configurable;

public abstract class LongTask extends Configurable {
	
	public class LongTaskUpdate {}

	private String taskType;
	private double currentPercent;
	private String currentMessage;
	
	protected abstract Object execute(Object... params);
	
	protected <T> T startTask(String taskType, Object... params) {
		this.taskType = taskType;
		updateStatus(0.0, "start task " + taskType);
		Object ret = execute(params);
		updateStatus(1.0, "task " + taskType + " finished");
		return (T)ret;
	}
	
	protected void startAnotherTaskAndWait(double percentAtEnd, LongTask task, String taskName, Object... params) {
		final double percentAtStart = currentPercent;
		final double ratio = percentAtEnd - percentAtStart;
		Observer temp = new Observer() {
			@Override
			public void update(Observable o, Object m) {
				if (m instanceof LongTaskUpdate) {
					LongTask task = (LongTask)o;
					LongTask.this.updateStatus(percentAtStart + task.getCurrentPercent()*ratio,
						String.format("%6.2f%% of %s: %s", task.getCurrentPercent()*100, task, task.getCurrentMessage()));
				}
			}
		};
		task.addObserver(temp);
		task.startTask(taskName, params);
		task.deleteObserver(temp);
	}
	
	public String getTaskType() {
		return taskType;
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
