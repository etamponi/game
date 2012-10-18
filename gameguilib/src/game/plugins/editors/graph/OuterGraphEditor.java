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
package game.plugins.editors.graph;

import game.core.blocks.Graph;
import game.editorsystem.ControlledEditor;
import game.editorsystem.EditorWindow;
import game.editorsystem.Editor;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;

public class OuterGraphEditor extends Editor {
	
	public static class GraphEditor extends ControlledEditor {
		
		@Override
		public Class getBaseEditableClass() {
			return GraphEditor.class;
		}

		@Override
		public boolean isInline() {
			return false;
		}
	}
	
	public Button editorButton = new Button("Edit graph");
	public GraphEditor editor = new GraphEditor();
	
	public OuterGraphEditor() {
		editorButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				editor.setReadOnly(isReadOnly());
				new EditorWindow(editor).startEdit(getModel());
			}
		});
	}

	@Override
	public Class getBaseEditableClass() {
		return Graph.class;
	}

	@Override
	public boolean isInline() {
		return true;
	}

	@Override
	public Node getView() {
		return editorButton;
	}

	@Override
	public void updateView() {
		/*
		if (((Graph)getModel().getContent()).trained)
			setReadOnly(true);
		*/
		if (getModel() != null) {
			editorButton.setText((isReadOnly() ? "View" : "Edit") + " graph: " + getModel().getContent());
		}
	}

}
