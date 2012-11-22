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
package game.main;

import game.core.Experiment;
import game.core.Result;
import game.utils.Log;

import java.util.concurrent.Executor;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventType;

import com.ios.Observer;
import com.ios.Property;

@SuppressWarnings("deprecation")
public class ExperimentService extends Service<Result> {
	
	public static final EventType<Event> FINISHED = new EventType<>();
	
	private static class SimpleThreadExecutor implements Executor {
		
		private Thread thread;

		@Override
		public void execute(Runnable command) {
			if (thread != null)
				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			thread = new Thread(command);
			thread.start();
		}
		
		public Thread getThread() {
			return thread;			
		}
		
	}
	
	private SimpleThreadExecutor executor = new SimpleThreadExecutor();
	
	private IntegerProperty counter = new SimpleIntegerProperty(0);
	private StringProperty currentExperimentName = new SimpleStringProperty("");
	
	private MainController controller;
	
	private boolean paused = false;
	private boolean stopped = false;
	private boolean finished = true;
	
	private Experiment currentExperiment;
	private Observer experimentObserver;
	
	public ExperimentService(MainController controller) {
		super();
		setExecutor(executor);
		this.controller = controller;
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
		try {
			executor.getThread().join();
			
			if (currentExperiment != null && experimentObserver != null) {
				experimentObserver.detach();
				currentExperiment = null;
				experimentObserver = null;
			}
			
		} catch (InterruptedException e) {}
	}
	
	public boolean isPaused() {
		return paused;
	}
	
	public boolean isStopped() {
		return stopped;
	}
	
	public void startList() { 
		counter.set(0);
		currentExperimentName.set(controller.experimentList.get(0).toString());
		paused = false;
		stopped = false;
		finished = false;
		
		start();
	}
	
	public IntegerProperty counterProperty() {
		return counter;
	}
	
	public StringProperty currentExperimentProperty() {
		return currentExperimentName;
	}
	
	public boolean hasFinished() {
		return finished;
	}

	@Override
	protected Task<Result> createTask() {
		return new Task<Result>() {
			@Override
			protected Result call() throws Exception {
				currentExperiment = (Experiment)controller.experimentList.get(counter.get());
				
				experimentObserver = new Observer(currentExperiment) {
					@Override
					public void action(Property changedPath) {
						if (changedPath.getPath().isEmpty()) {
							Log.write(currentExperiment, "%5.1f%%: %s", currentExperiment.getProgress()*100, currentExperiment.getMessage());
							updateMessage(currentExperiment.getMessage());
							updateProgress((long)(currentExperiment.getProgress()*100), 100);
						}
					}
				};
				
				Result ret = currentExperiment.execute(Settings.RESULTSDIR);

				experimentObserver.detach();
				
				currentExperiment = null;
				experimentObserver = null;
				return ret;
			}
		};
	}

	@Override
	protected void succeeded() {
		super.succeeded();
		Result result = getValue();
		if (controller.addToResultList())
			controller.getResultListController().addResult(result);
		counter.set(counter.get()+1);
		if (counter.get() < controller.experimentList.size()) {
			reset();
			currentExperimentName.set(controller.experimentList.get(counter.get()).toString());
			start();
		} else {
			finished = true;
			fireEvent(new Event(FINISHED));
		}
	}
	
}
