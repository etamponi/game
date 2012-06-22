package game.plugins.editors;

import game.configuration.Configurable.Change;
import game.editorsystem.Editor;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;

public class FileEditor extends Editor {
	
	HBox line = new HBox();
	
	TextField pathField = new TextField();
	Button browseButton = new Button("...");
	
	public FileEditor() {
		browseButton.setPrefWidth(40);
		line.getChildren().addAll(pathField, browseButton);
		line.setSpacing(15);
		HBox.setHgrow(pathField, Priority.ALWAYS);
		
		browseButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser chooser = new FileChooser();
				chooser.setTitle("Open file");
				chooser.setInitialDirectory(new File(System.getProperty("user.home")));
				File file = chooser.showOpenDialog(browseButton.getScene().getWindow());
				if (file != null) {
					pathField.setText(file.getAbsolutePath());
					if (getModel() != null) {
						getModel().setContent(file);
					}
				}
			}
		});
		
		pathField.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (getModel() != null) {
					getModel().setContent(new File(pathField.getText()));
				}
			}
		});
	}

	@Override
	public Node getView() {
		return line;
	}

	@Override
	public void connectView() {
		pathField.setText(((File)getModel().getContent()).getAbsolutePath());
	}

	@Override
	public void updateView(Change change) {
		connectView();
	}

	@Override
	public boolean isInline() {
		return true;
	}

	@Override
	public Class getBaseEditableClass() {
		return File.class;
	}

}
