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

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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
	
	@FXML
	public void onStart(ActionEvent event) {
		Task overallTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				ConfigurableList experiments = model.getContent();
				double step = 99 / experiments.size();
				updateProgress(1, 100);
				double progress = 1;
				for (int i = 0; i < experiments.size(); i++) {
					Thread.sleep(500);
					progress += step;
					updateProgress((long)progress, 100);
				}
				
				return null;
			}
		};
		overallProgress.progressProperty().bind(overallTask.progressProperty());
		new Thread(overallTask).start();
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
			if (((List)model.getContent()).isEmpty() || !((Configurable)model.getContent()).getConfigurationErrors().isEmpty()) {
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
