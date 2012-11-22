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

import game.editorsystem.EditorWindow;
import game.editorsystem.PropertyEditor;
import game.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

import com.ios.IObject;
import com.ios.listeners.SubPathListener;

public class ImplementationChooserEditor extends PropertyEditor {
	
	public static class Implementation implements Comparable<Implementation> {
		
		private IObject instance;
		
		public Implementation(IObject instance) {
			this.instance = instance;
		}
		
		public Implementation(Class<? extends IObject> type) {
			try {
				instance = type.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				System.out.println("Cannot instantiate implementation for " + type);
			}
		}
		
		public IObject getInstance() {
			return instance;
		}
		
		@Override
		public String toString() {
			if (instance == null)
				return "<null>";
			else
				return instance.getClass().getSimpleName();
		}

		@Override
		public int compareTo(Implementation o) {
			return this.toString().compareTo(o.toString());
		}
		
	}
	
	private ChangeListener<Implementation> changeListener = new ChangeListener<Implementation>() {
		@Override
		public void changed(ObservableValue<? extends Implementation> observable,
				Implementation oldValue, Implementation newValue) {
			if (getModel() == null)
				return;
			IObject selected = box.getValue().getInstance();
			if (getModel().getContent() != selected)
				updateModel(selected);
		}
	};
	
	private HBox container = new HBox();
	
	private ComboBox<Implementation> box = new ComboBox<>();

	private Button editButton;
	
	public ImplementationChooserEditor() {
		getUpdateTrigger().getListeners().add(new SubPathListener(getProperty("root")));
		
		this.editButton = new Button("Edit");
		editButton.setPrefWidth(55);
		editButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (getModel().getContent() == null)
					return;
				
				PropertyEditor editor = PropertyEditor.getBestEditor(getModel().getContentType(true));
				editor.setReadOnly(isReadOnly());
				new EditorWindow(editor).startEdit(getModel());
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
			box.getSelectionModel().selectedItemProperty().removeListener(changeListener);
			box.getItems().clear();
			
			if (getModel() != null) {
				List<Implementation> implementations = new ArrayList<>();
				Implementation selected = new Implementation((IObject)null);
				
				IObject current = getModel().getContent();
				implementations.add(selected);
				
				Set<Class> types = getModel().getCompatibleContentTypes();
				for (Class<? extends IObject> type: types) {
					if (current != null && current.getClass().equals(type)) {
						selected = new Implementation(current);
						implementations.add(selected);
					} else {
						implementations.add(new Implementation(type));
					}
				}
				
				Collections.sort(implementations);
				box.getItems().addAll(implementations);
				box.getSelectionModel().select(selected);
	
				box.getSelectionModel().selectedItemProperty().addListener(changeListener);
			}
			editButton.setText("Edit");
		}
	}

	private Node getReadOnlyBox() {
		TextField field = new TextField(new Implementation(getModel().getContent(IObject.class)).toString());
		field.setEditable(false);
		HBox.setHgrow(field, Priority.ALWAYS);
		return field;
	}

	@Override
	public boolean canEdit(Class type) {
		return !Utils.isConcrete(type) && getBaseEditableClass().isAssignableFrom(type);
	}

	@Override
	public Class getBaseEditableClass() {
		return IObject.class;
	}

	@Override
	public boolean isInline() {
		return true;
	}

}
