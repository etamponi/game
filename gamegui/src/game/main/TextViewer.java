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
package game.main;

import game.core.Result;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class TextViewer extends Stage {
	
	public TextViewer(Result evaluation) {
		AnchorPane pane = new AnchorPane();
		TextArea content = new TextArea(evaluation.prettyPrint());
		content.setStyle("-fx-font-family: monospace;");
		content.setEditable(false);
		content.setPrefSize(800, 120);
		AnchorPane.setTopAnchor(content, 14.0);
		AnchorPane.setLeftAnchor(content, 14.0);
		AnchorPane.setRightAnchor(content, 14.0);
		AnchorPane.setBottomAnchor(content, 14.0);
		pane.getChildren().add(content);
		setScene(new Scene(pane));
	}
	
}