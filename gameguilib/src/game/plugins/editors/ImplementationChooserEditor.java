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

import game.configuration.Change;
import game.configuration.Configurable;
import game.editorsystem.EditorWindow;
import game.editorsystem.Editor;
import game.plugins.Implementation;
import game.utils.Utils;

import java.util.Observable;
import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class ImplementationChooserEditor extends Editor {
	
	private ChangeListener<Implementation> listener = new ChangeListener<Implementation>() {
		@Override
		public void changed(
				ObservableValue<? extends Implementation> observable,
				Implementation oldValue, Implementation newValue) {
			if (getModel() == null)
				return;
			Configurable selected = box.getValue().getContent();
			if (getModel().getContent() != selected)
				setModelContent(selected);
		}
	};
	
	private HBox container = new HBox();
	
	private ComboBox<Implementation<Configurable>> box = new ComboBox<Implementation<Configurable>>();

	private Button editButton;
	
	public ImplementationChooserEditor() {
		this.editButton = new Button("Edit");
		editButton.setPrefWidth(55);
		editButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (getModel().getContent() == null)
					return;
				
				Editor editor = getModel().getBestEditor(true);
				editor.setReadOnly(isReadOnly());
				EditorWindow window = new EditorWindow(editor);
				window.startEdit(getModel());
			}
		});
		container.setSpacing(15);
		container.getChildren().addAll(box, editButton);
		box.prefWidthProperty().bind(container.widthProperty().subtract(editButton.prefWidthProperty()).subtract(5));
	}

	@Override
	public Node getView() {
		return container;
	}

	@Override
	public void updateView() {
		if (isReadOnly()) {
			container.getChildren().set(0, getReadOnlyBox());
			editButton.setText("View");
		} else {
			container.getChildren().set(0, box);
			box.getSelectionModel().selectedItemProperty().removeListener(listener);
			box.getItems().clear();
			
			if (getModel() != null) {
				Object current = getModel().getContent();
				box.getItems().add(new Implementation(null));
				if (current == null)
					box.getSelectionModel().select(0);
				
				Set<Implementation<Configurable>> implementations = getModel().getCompatibleImplementations();
				for (Implementation<Configurable> impl: implementations) {
					if (current != null && current.getClass().equals(impl.getContent().getClass())) {
						box.getItems().add(new Implementation(current));
						box.getSelectionModel().select(box.getItems().size()-1);
					} else
						box.getItems().add(impl);
				}
	
				box.getSelectionModel().selectedItemProperty().addListener(listener);
			}
			editButton.setText("Edit");
		}
	}

	private Node getReadOnlyBox() {
		TextField field = new TextField(new Implementation(getModel().getContent()).toString());
		field.setEditable(false);
		HBox.setHgrow(field, Priority.ALWAYS);
		return field;
	}

	@Override
	public void update(Observable observed, Object message) {
		if (message instanceof Change) {
			if (((Change)message).getSetter() != this)
				updateView();
		}
	}

	@Override
	public boolean canEdit(Class type) {
		return !Utils.isConcrete(type) && getBaseEditableClass().isAssignableFrom(type);
	}

	@Override
	public Class getBaseEditableClass() {
		return Configurable.class;
	}

	@Override
	public boolean isInline() {
		return true;
	}

}
