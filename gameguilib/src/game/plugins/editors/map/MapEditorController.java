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
package game.plugins.editors.map;

import game.editorsystem.EditorController;
import game.editorsystem.EditorWindow;
import game.editorsystem.PropertyEditor;
import game.plugins.editors.StringEditor;

import java.net.URL;
import java.util.ResourceBundle;

import com.ios.IMap;
import com.ios.IObject;
import com.ios.Property;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

public class MapEditorController implements EditorController {
	
	private Property model;
	
	@FXML
	private ListView<Property> listView;
	@FXML
	private Button addKeyButton;
	@FXML
	private Button removeButton;
	@FXML
	private Button editButton;
	@FXML
	private Button editKeyButton;

	private PropertyEditor editor;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	}
	
	@Override
	public void setModel(Property model) {
		this.model = model;
	}

	@Override
	public void updateView() {
		IMap map = model.getContent();
		if (map == null)
			return;
		
		ObservableList<Property> items = FXCollections.<Property>observableArrayList();
		for (Object key: map.keySet()) {
			items.add(new Property((IMap)map, key.toString()));
		}
		//listView.getSelectionModel().select(-1);
		listView.getSelectionModel().clearSelection();
		listView.setItems(items);
		
		boolean disable = editor.isReadOnly();
		addKeyButton.setDisable(disable);
		editKeyButton.setDisable(disable);
		removeButton.setDisable(disable);
		if (editor.isReadOnly())
			editButton.setText("View");
		else
			editButton.setText("Edit");
	}
	
	private static class Temporary extends IObject {
		@SuppressWarnings("unused")
		public String key;
		
		public Temporary(String key) {
			this.key = key;
		}
	}
	
	@FXML
	public void addKeyAction(ActionEvent event) {
		IMap map = model.getContent();
		if (map == null)
			return;
		
		Property key = new Property(new Temporary(""), "key");
		StringEditor editor = new StringEditor();
		new EditorWindow(editor).startEdit(key);
		
		map.put((String)key.getContent(), null);
		
		selectKey((String)key.getContent());
		editAction(event);
	}
	
	private void selectKey(String key) {
		for (Property option: listView.getItems()) {
			if (option.getPath().equals(key)) {
				listView.getSelectionModel().select(option);
				return;
			}
		}
	}
	
	public void editKeyAction(ActionEvent event) {
		IMap map = model.getContent();
		if (map == null)
			return;
		
		String oldKey = listView.getSelectionModel().getSelectedItem().getPath();
		Property key = new Property(new Temporary(listView.getSelectionModel().getSelectedItem().getContent(String.class)), "key");
		StringEditor editor = new StringEditor();
		new EditorWindow(editor).startEdit(key);
		
		Object value = map.get(oldKey);
		map.remove(oldKey);
		map.put(key.getContent(String.class), value);
		
		selectKey(key.getContent(String.class));
	}

	@FXML
	public void removeAction(ActionEvent event) {
		IMap map = model.getContent();
		if (map == null)
			return;
		
		Property option = listView.getSelectionModel().getSelectedItem();
		if (option != null) {
			map.remove(option.getPath());
		}
	}
	
	@FXML
	public void editAction(ActionEvent event) {
		if (model.getContent() == null)
			return;
		
		Property option = listView.getSelectionModel().getSelectedItem();
		if (option != null) {
			PropertyEditor editor = PropertyEditor.getBestEditor(option.getContentType(true));
			if (editor != null) {
				editor.setReadOnly(this.editor.isReadOnly());
				new EditorWindow(editor).startEdit(option);
			}
		}
	}

	@Override
	public void setEditor(PropertyEditor editor) {
		this.editor = editor;
	}

	@Override
	public PropertyEditor getEditor() {
		return editor;
	}
	
}
