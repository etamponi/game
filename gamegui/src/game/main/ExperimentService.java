package game.main;

import game.configuration.ConfigurableList;
import game.core.Experiment;
import game.core.LongTask.LongTaskUpdate;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executor;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventType;

@SuppressWarnings("deprecation")
public class ExperimentService extends Service<Void> {
	
	public static final EventType<Event> FINISHED = new EventType<>();
	
	private static class SimpleThreadExecutor implements Executor {
		
		private Thread thread;

		@Override
		public void execute(Runnable command) {
			thread = new Thread(command);
			thread.start();
		}
		
		public Thread getThread() {
			return thread;			
		}
		
	}
	
	private SimpleThreadExecutor executor = new SimpleThreadExecutor();
	
	private IntegerProperty counter = new SimpleIntegerProperty(0);
	private StringProperty currentExperiment = new SimpleStringProperty("");
	private List<Experiment> experiments;
	
	private boolean paused = false;
	private boolean stopped = false;
	
	public ExperimentService() {
		setExecutor(executor);
	}
	
	public void pause() {
		if (!paused) {
			executor.getThread().suspend();
			paused = true;
		}
	}
	
	public void resume() {
		if (paused) {
			executor.getThread().resume();
			paused = false;
		}
	}
	
	public void stop() {
		stopped = true;
		executor.getThread().stop();
	}
	
	public boolean isPaused() {
		return paused;
	}
	
	public boolean isStopped() {
		return stopped;
	}
	
	public void start(ConfigurableList list) {
		experiments = list.getList(Experiment.class);
		counter.set(0);
		currentExperiment.set(experiments.get(0).toString());
		paused = false;
		stopped = false;
		
		start();
	}
	
	public IntegerProperty counterProperty() {
		return counter;
	}
	
	public StringProperty currentExperimentProperty() {
		return currentExperiment;
	}

	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				final Experiment e = experiments.get(counter.get());
				Observer o = new Observer() {
					@Override
					public void update(Observable obs, Object m) {
						if (m instanceof LongTaskUpdate) {
							updateMessage(e.getCurrentMessage());
							updateProgress((long)(e.getCurrentPercent()*100), 100);
//							try {
//								Thread.sleep(100);
//							} catch (InterruptedException e) {}
						}
					}
				};
				e.addObserver(o);
				e.startExperiment();
				e.deleteObserver(o);
				return null;
			}
		};
	}

	@Override
	protected void succeeded() {
		super.succeeded();
		counter.set(counter.get()+1);
		if (counter.get() < experiments.size()) {
			reset();
			currentExperiment.set(experiments.get(counter.get()).toString());
			start();
		} else {
			fireEvent(new Event(FINISHED));
		}
	}
	
}