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

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EditorWindow extends Stage {
	
	private OptionEditor editor;
	
	public EditorWindow(OptionEditor editor) {
		assert(editor != null);
		
		this.editor = editor;
		
		setTitle(editor.getClass().getSimpleName());
		initModality(Modality.APPLICATION_MODAL);
		
		AnchorPane root = new AnchorPane();
		
		Node view = editor.getView();
		AnchorPane.setTopAnchor(view, 0.0);
		AnchorPane.setLeftAnchor(view, 0.0);
		AnchorPane.setRightAnchor(view, 0.0);
		AnchorPane.setBottomAnchor(view, 0.0);
		
		root.getChildren().add(view);
		root.setMinWidth(200);
		
		setScene(new Scene(root));
	}
	
	public void startEdit(Option model) {
		editor.connect(model);
		showAndWait();
		editor.disconnect();
	}

}

