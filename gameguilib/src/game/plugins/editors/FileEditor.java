/*******************************************************************************
 * Copyright (c) 2012 Emanuele.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Emanuele - initial API and implementation
 ******************************************************************************/
package game.plugins.editors;

import game.editorsystem.PropertyEditor;
import game.utils.Utils;

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

public class FileEditor extends PropertyEditor {
	
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
				File currentFile = getModel().getContent();
				if (currentFile.getParentFile() != null)
					chooser.setInitialDirectory(currentFile.getParentFile());
				else
					chooser.setInitialDirectory(new File(System.getProperty("user.dir")));
				File file = chooser.showOpenDialog(browseButton.getScene().getWindow());
				if (file != null) {
					pathField.setText(Utils.relativize(file));
					if (getModel() != null) {
						updateModel(new File(pathField.getText()));
					}
				}
			}
		});
		
		pathField.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (getModel() != null) {
					updateModel(new File(pathField.getText()));
				}
			}
		});
	}

	@Override
	public Node getView() {
		return line;
	}

	@Override
	public void updateView() {
		if (getModel().getContent() == null)
			updateModel(new File("nonexistent.txt"));
		pathField.setText(Utils.relativize((File)getModel().getContent()));
		
		pathField.setEditable(!isReadOnly());
		browseButton.setDisable(isReadOnly());
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
