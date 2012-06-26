package game.main;

import game.configuration.Change;
import game.configuration.Configurable;
import game.configuration.ConfigurableList;
import game.core.Experiment;
import game.editorsystem.Editor;
import game.editorsystem.EditorWindow;
import game.editorsystem.Option;
import game.plugins.editors.ConfigurableEditor;
import game.plugins.editors.ConfigurableEditor.SerializationEditor;
import game.plugins.editors.configurablelist.ConfigurableListEditor;

import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainController extends Configurable implements Initializable {
	
	private Option model = new Option(new ConfigurableList(this, Experiment.class));
	
	ExperimentService service;

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
		
		addPluginManagerEditorToToolBar((ToolBar)serialization.getView());
		
		Editor editor = new ConfigurableListEditor();
		editor.setModel(model);
		root.getChildren().addAll(serialization.getView(), editor.getView());
		root.setSpacing(10);
		root.setStyle("-fx-border-color:-fx-color; -fx-padding: 0px 0px 5px 0px;");
		VBox.setVgrow(editor.getView(), Priority.ALWAYS);
		disableButtons(true, true, true);
	}
	
	private void addPluginManagerEditorToToolBar(ToolBar toolbar) {
		Button pmButton = new Button("Plugins");
		pmButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Editor editor = new ConfigurableEditor();
				editor.setModel(new Option(Settings.getInstance().getPluginManager()));
				new EditorWindow(editor).showAndWait();
				Settings.getInstance().getPluginManager().saveConfiguration(Settings.CONFIGFILE);
			}
		});
		toolbar.getItems().addAll(new Separator(), pmButton);
	}
	
	@FXML
	public void onStart(ActionEvent event) {
		ConfigurableList experiments = model.getContent();
		service = new ExperimentService();
		
		currentProgress.progressProperty().bind(service.progressProperty());
		currentMessage.textProperty().bind(service.messageProperty());
		
		double step = 1.0 / experiments.size();
		overallProgress.progressProperty().bind(
				(service.progressProperty().divide(experiments.size())).add(service.counterProperty().multiply(step)));
		overallMessage.textProperty().bind(service.currentExperimentProperty());
		
		service.setOnFailed(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				if (!service.isStopped())
					service.getException().printStackTrace();
				disableButtons(false, true, true);
			}
		});

		service.addEventHandler(ExperimentService.FINISHED, new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				disableButtons(false, true, true);
			}
		});

		service.start(experiments);
		disableButtons(true, false, false);
	}
	
	@FXML
	public void onPause(ActionEvent event) {
		if (pauseButton.getText().equals("Pause")) {
			service.pause();
			pauseButton.setText("Resume");
		} else {
			service.resume();
			pauseButton.setText("Pause");
		}
	}
	
	private boolean confirmationDialog() {
		final BooleanProperty sure = new SimpleBooleanProperty(false);
		final Stage stage = new Stage();
		
		VBox container = new VBox();
		HBox buttons = new HBox();
		Button yes = new Button("Yes");
		Button no = new Button("No");
		yes.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				sure.set(true);
				stage.close();
			}
		});
		no.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				stage.close();
			}
		});
		buttons.setSpacing(15);
		buttons.getChildren().addAll(yes, no);
		container.setSpacing(15);
		container.setPadding(new Insets(15));
		container.getChildren().addAll(new Label("Do you want to stop all the experiments?"), buttons);
		buttons.setAlignment(Pos.CENTER);
		stage.setScene(new Scene(container));
		stage.showAndWait();
		return sure.get();
	}
	
	@FXML
	public void onStop(ActionEvent event) {
		boolean alreadyPaused = service.isPaused();
		if (!alreadyPaused)
			service.pause();
		
		if (confirmationDialog()) {
			service.stop();
			disableButtons(false, true, true);
			pauseButton.setText("Pause");
			overallMessage.textProperty().unbind();
			overallMessage.setText("");
			currentMessage.textProperty().unbind();
			currentMessage.setText("");
			overallProgress.progressProperty().unbind();
			overallProgress.setProgress(0);
			currentProgress.progressProperty().unbind();
			currentProgress.setProgress(0);
		} else {
			if (!alreadyPaused)
				service.resume();
		}
	}

	@Override
	public void update(Observable observedOption, Object message) {
		if (message instanceof Change) {
			ConfigurableList experiments = model.getContent();
			if (experiments.isEmpty() || !experiments.getConfigurationErrors().isEmpty()) {
				disableButtons(true, true, true);
			} else {
				disableButtons(false, true, true);
			}
		}
	}

	private void disableButtons(boolean start, boolean pause, boolean stop) {
		startButton.setDisable(start);
		pauseButton.setDisable(pause);
		stopButton.setDisable(stop);
	}

}
