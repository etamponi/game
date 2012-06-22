package game.main;

import game.configuration.Configurable;
import game.configuration.ConfigurableList;
import game.core.Experiment;
import game.editorsystem.Editor;
import game.editorsystem.Option;
import game.plugins.editors.ConfigurableEditor.SerializationEditor;
import game.plugins.editors.configurablelist.ConfigurableListEditor;

import java.net.URL;
import java.util.List;
import java.util.Observable;
import java.util.ResourceBundle;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

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
	private Label message;
	@FXML
	private ProgressBar overallProgress;
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
	}
	
	private static class ExperimentService extends Service<Void> {
		
		private IntegerProperty counter = new SimpleIntegerProperty(0);
		private List<Experiment> experiments;
		
		public void setExperiments(ConfigurableList list) {
			experiments = list.getList(Experiment.class);
		}
		
		public int getCounter() {
			return counter.get();
		}
		
		public IntegerProperty counterProperty() {
			return counter;
		}

		@Override
		protected Task<Void> createTask() {
			return new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					updateProgress(0, 100);
					Thread.sleep(500);
					updateProgress(50, 100);
					Thread.sleep(500);
					updateProgress(100, 100);
					Thread.sleep(500);
					//experiment.startExperiment();
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
				start();
			}
		}
		
	}
	
	@FXML
	public void onStart(ActionEvent event) {
		ConfigurableList experiments = model.getContent();
		
		ExperimentService service = new ExperimentService();
		currentProgress.progressProperty().bind(service.progressProperty());
		
		double step = 1.0 / experiments.size();
		overallProgress.progressProperty().bind(
				(service.progressProperty().divide(experiments.size())).add(service.counterProperty().multiply(step)));
		
		service.setExperiments(experiments);
		service.start(); 
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
		pauseButton.setDisable(disable);
		stopButton.setDisable(disable);
	}

}
