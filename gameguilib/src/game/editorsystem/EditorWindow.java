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
package game.editorsystem;

import com.ios.IObject;
import com.ios.Property;

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
	
	private PropertyEditor editor;
	
	private Object original;

	private Button cancelButton;
	
	public EditorWindow(PropertyEditor e) {
		this(e, true);
	}
	
	public EditorWindow(PropertyEditor e, boolean modal) {
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
				editor.detach();
				close();
			}
		});
		controls.getChildren().add(okButton);
		
		cancelButton = new Button("Cancel");
		cancelButton.setPrefWidth(70);
		cancelButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Property model = editor.getModel();
				editor.detach();
				model.setContent(original);
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
				if (!editor.isReadOnly()) {
					Property model = editor.getModel();
					editor.detach();
					model.setContent(original);
				}
			}
		});
	}
	
	public void startEdit(Property model) {
		if (!editor.isReadOnly()) {
			if (model.getContent() instanceof IObject) {
				original = model.getContent(IObject.class).copy();
			} else {
				original = IObject.getKryo().copy(model.getContent());
			}
		}
		
		editor.connect(model);
		if (model.getContent() != null)
			setTitle(model.toString());
		else
			setTitle(model.getPath() + ": new " + model.getContentType(false).getSimpleName());
		
		cancelButton.setDisable(editor.isReadOnly());
		
		showAndWait();
		
		if (!editor.isReadOnly() && model.getContent() instanceof IObject)
			model.getContent(IObject.class).editingFinished();
		
		editor.detach();
	}

}
