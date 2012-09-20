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
package game.plugins.editors.metrics;

import game.core.Metric;
import game.editorsystem.Editor;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

public class TextMetricViewer extends Editor {
	
	private AnchorPane pane = new AnchorPane();
	private TextArea content = new TextArea();
	
	public TextMetricViewer() {
		content.setStyle("-fx-font-family: monospace;");
		content.setEditable(false);
		content.setPrefSize(800, 120);
		AnchorPane.setTopAnchor(content, 14.0);
		AnchorPane.setLeftAnchor(content, 14.0);
		AnchorPane.setRightAnchor(content, 14.0);
		AnchorPane.setBottomAnchor(content, 14.0);
		pane.getChildren().add(content);
	}

	@Override
	public Node getView() {
		return pane;
	}

	@Override
	public void updateView() {
		if (getModel() == null)
			content.setText("");
		else
			content.setText(((Metric)getModel().getContent()).prettyPrint());
	}

	@Override
	public boolean isInline() {
		return false;
	}

	@Override
	public Class getBaseEditableClass() {
		return Metric.class;
	}
	
}
