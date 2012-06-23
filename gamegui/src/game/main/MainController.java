package game.main;

import game.configuration.Configurable;
import game.configuration.ConfigurableList;
import game.core.Experiment;
import game.core.LongTask.LongTaskUpdate;
import game.editorsystem.Editor;
import game.editorsystem.Option;
import game.plugins.editors.ConfigurableEditor.SerializationEditor;
import game.plugins.editors.configurablelist.ConfigurableListEditor;

import java.net.URL;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class MainController extends Configurable implements Initializable {
	
	private Option model = new Option(new ConfigurableList(this, Experiment.class));

	@FXML
	private VBox root;
	@FXML
	private Button startButton;
	@FXML
	private Button pauseButton;
	@FXML
	private Button stopButton;
	@FXML
	private Text overallMessage;
	@FXML
	private ProgressBar overallProgress;
	@FXML
	private Text currentMessage;
	@FXML
	private ProgressBar currentProgress;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Editor serialization = new SerializationEditor();
		serialization.setModel(model);
		Editor editor = new ConfigurableListEditor();
		editor.setModel(model);
		root.getChildren().addAll(serialization.getView(), editor.getView());
		root.setSpacing(10);
		root.setStyle("-fx-border-color:-fx-color; -fx-padding: 0px 0px 5px 0px;");
		VBox.setVgrow(editor.getView(), Priority.ALWAYS);
		disableButtons(true);
	}
	
	private static class ExperimentService extends Service<Void> {
		
		private IntegerProperty counter = new SimpleIntegerProperty(0);
		private StringProperty currentExperiment = new SimpleStringProperty("");
		private List<Experiment> experiments;
		
		public void setExperiments(ConfigurableList list) {
			experiments = list.getList(Experiment.class);
			counter.set(0);
			currentExperiment.set(experiments.get(0).toString());
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
			}
		}
		
	}
	
	@FXML
	public void onStart(ActionEvent event) {
		ConfigurableList experiments = model.getContent();
		
		final ExperimentService service = new ExperimentService();
		currentProgress.progressProperty().bind(service.progressProperty());
		currentMessage.textProperty().bind(service.messageProperty());
		
		double step = 1.0 / experiments.size();
		overallProgress.progressProperty().bind(
				(service.progressProperty().divide(experiments.size())).add(service.counterProperty().multiply(step)));
		overallMessage.textProperty().bind(service.currentExperimentProperty());
		
		service.setExperiments(experiments);
		service.start();
		
		service.setOnFailed(new EventHandler<WorkerStateEvent>() {
			
			@Override
			public void handle(WorkerStateEvent event) {
				service.getException().printStackTrace();
			}
		});
	}
	
	@FXML
	public void onPause(ActionEvent event) {
		
	}
	
	@FXML
	public void onStop(ActionEvent event) {
		
	}

	@Override
	public void update(Observable observedOption, Object message) {
		if (message instanceof Change) {
			ConfigurableList experiments = model.getContent();
			if (experiments.isEmpty() || !experiments.getConfigurationErrors().isEmpty()) {
				disableButtons(true);
			} else {
				disableButtons(false);
			}
		}
	}

	private void disableButtons(boolean disable) {
		startButton.setDisable(disable);
		pauseButton.setDisable(true);
		stopButton.setDisable(true);
	}

}
