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
package game.editorsystem;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class EditorWindow extends Stage {
	
	private Editor editor;
	
	private Object original;

	private Button cancelButton;
	
	public EditorWindow(Editor e) {
		this(e, true);
	}
	
	public EditorWindow(Editor e, boolean modal) {
		assert(e != null);
		
		this.editor = e;
		
		if (modal)
			initModality(Modality.APPLICATION_MODAL);
		
		VBox layout = new VBox(5);
		layout.setPadding(new Insets(5));
		layout.setMinWidth(400);
		
		AnchorPane root = new AnchorPane();
		VBox.setVgrow(root, Priority.ALWAYS);
		layout.getChildren().add(root);
		
		HBox controls = new HBox(5);
		controls.setPadding(new Insets(5));
		controls.setAlignment(Pos.CENTER);
		layout.getChildren().add(controls);
		
		Button okButton = new Button("OK");
		okButton.setPrefWidth(70);
		okButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				editor.disconnect();
				close();
			}
		});
		controls.getChildren().add(okButton);
		
		cancelButton = new Button("Cancel");
		cancelButton.setPrefWidth(70);
		cancelButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Option model = editor.getModel();
				editor.disconnect();
				if (model.getContent() != null && !model.getContent().equals(original)) {
					model.setContent(original);
				}
				close();
			}
		});
		controls.getChildren().add(cancelButton);
		
		Node view = e.getView();
		AnchorPane.setTopAnchor(view, 0.0);
		AnchorPane.setLeftAnchor(view, 0.0);
		AnchorPane.setRightAnchor(view, 0.0);
		AnchorPane.setBottomAnchor(view, 0.0);
		root.getChildren().add(view);
		
		setScene(new Scene(layout));
		
		setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				Option model = editor.getModel();
				editor.disconnect();
				if (model.getContent() != null && !model.getContent().equals(original)) {
					model.setContent(original);
				}
			}
		});
	}
	
	public void startEdit(Option model) {
		original = model.cloneContent();
		
		editor.connect(model);
		if (model.getContent() != null)
			setTitle(model.getContent().toString());
		else
			setTitle(model.getType().getSimpleName());
		
		cancelButton.setDisable(editor.isReadOnly());
		
		showAndWait();
	}

}

