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

import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EditorWindow extends Stage {
	
	public EditorWindow(Editor editor) {
		initModality(Modality.APPLICATION_MODAL);
		
		AnchorPane root = new AnchorPane();
		root.getChildren().add(editor.getView());
		
		setScene(new Scene(root));
	}

}
