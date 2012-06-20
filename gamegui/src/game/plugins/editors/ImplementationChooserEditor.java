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
package game.plugins.editors;

import game.configuration.Configurable;
import game.configuration.Configurable.Change;
import game.editorsystem.Editor;
import game.editorsystem.EditorWindow;
import game.editorsystem.Option;
import game.utils.Utils;

import java.util.Observable;
import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class ImplementationChooserEditor extends Editor {
	
	public class Implementation {
		
		public Object content;
		
		public Implementation(Object content) {
			this.content = content;
		}
		
		public Object getContent() {
			return content;
		}
		
		@Override
		public String toString() {
			return content.getClass().getSimpleName();
		}
		
	}
	
	private ChangeListener<Implementation> listener = new ChangeListener<Implementation>() {
		@Override
		public void changed(
				ObservableValue<? extends Implementation> observable,
				Implementation oldValue, Implementation newValue) {
			if (getModel() == null)
				return;
			Object selected = box.getValue() == null ? null : box.getValue().getContent();
			if (getModel().getContent() != selected)
				getModel().setContent(selected);
		}
	};
	
	private HBox container = new HBox();
	
	private ChoiceBox<Implementation> box = new ChoiceBox<>();
	
	public ImplementationChooserEditor() {
		Button editButton = new Button("Edit");
		editButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (getModel().getContent() == null)
					return;
				
				Option option = new Option((Configurable)getModel().getContent());
				Editor editor = option.getBestEditor();
				editor.setModel(option);
				EditorWindow window = new EditorWindow(editor);
				window.show();
			}
		});
		container.getChildren().addAll(box, editButton);
		HBox.setHgrow(editButton, Priority.ALWAYS);
	}

	@Override
	public Node getView() {
		return container;
	}

	@Override
	public void connectView() {
		Object current = getModel().getContent(); 
		
		box.getSelectionModel().selectedItemProperty().removeListener(listener);
		
		box.getItems().clear();
		box.getItems().add(null);
		if (current == null)
			box.getSelectionModel().select(0);
		if (getModel() != null) {
			Set<Object> contents = getModel().getCompatibleInstances();
			for (Object content: contents) {
				if (current != null && current.getClass().equals(content.getClass())) {
					box.getItems().add(new Implementation(current));
					box.getSelectionModel().select(box.getItems().size()-1);
				} else
					box.getItems().add(new Implementation(content));
			}
		}
		
		box.getSelectionModel().selectedItemProperty().addListener(listener);
	}

	@Override
	public void update(Observable observed, Object message) {
		if (message instanceof Change) {
			updateView((Change)message);
		}
	}

	@Override
	public void updateView(Change change) {
		connectView();
	}

	@Override
	public boolean canEdit(Class type) {
		return !Utils.isConcrete(type) && getBaseEditableClass().isAssignableFrom(type);
	}

	@Override
	public Class getBaseEditableClass() {
		return Configurable.class;
	}

}
