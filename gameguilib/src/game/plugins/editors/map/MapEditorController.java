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

import game.configuration.ConfigurableMap;
import game.editorsystem.Editor;
import game.editorsystem.EditorController;
import game.editorsystem.EditorWindow;
import game.editorsystem.Option;
import game.plugins.editors.StringEditor;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

public class MapEditorController implements EditorController {
	
	private Option model;
	
	@FXML
	private ListView<Option> listView;
	@FXML
	private Button addKeyButton;
	@FXML
	private Button removeButton;
	@FXML
	private Button editButton;
	@FXML
	private Button editKeyButton;

	private Editor editor;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	}
	
	@Override
	public void setModel(Option model) {
		this.model = model;
	}

	@Override
	public void updateView() {
		Map map = model.getContent();
		if (map == null)
			return;
		
		ObservableList<Option> items = FXCollections.<Option>observableArrayList();
		for (Object key: map.keySet()) {
			items.add(map instanceof ConfigurableMap ?
					                 new Option((ConfigurableMap)map, key.toString())
					               : new Option(map.get(key)));
		}
		//listView.getSelectionModel().select(-1);
		listView.getSelectionModel().clearSelection();
		listView.setItems(items);
		
		boolean disable = editor.isReadOnly() || !(map instanceof ConfigurableMap);
		addKeyButton.setDisable(disable);
		editKeyButton.setDisable(disable);
		removeButton.setDisable(disable);
		if (editor.isReadOnly())
			editButton.setText("View");
		else
			editButton.setText("Edit");
	}
	
	@FXML
	public void addKeyAction(ActionEvent event) {
		ConfigurableMap map = model.getContent();
		if (map == null)
			return;
		
		Option key = new Option(new String(""));
		StringEditor editor = new StringEditor();
		new EditorWindow(editor).startEdit(key);
		
		map.put((String)key.getContent(), null);
		
		selectKey((String)key.getContent());
		editAction(event);
	}
	
	private void selectKey(String key) {
		for (Option option: listView.getItems()) {
			if (option.getOptionName().equals(key)) {
				listView.getSelectionModel().select(option);
				return;
			}
		}
	}
	
	public void editKeyAction(ActionEvent event) {
		ConfigurableMap map = model.getContent();
		if (map == null)
			return;
		
		String oldKey = listView.getSelectionModel().getSelectedItem().getOptionName();
		Option key = new Option(listView.getSelectionModel().getSelectedItem().getOptionName());
		StringEditor editor = new StringEditor();
		new EditorWindow(editor).startEdit(key);
		
		Object value = map.get(oldKey);
		map.remove(oldKey);
		map.put(key.getContent(String.class), value);
		
		selectKey(key.getContent(String.class));
	}

	@FXML
	public void removeAction(ActionEvent event) {
		ConfigurableMap map = model.getContent();
		if (map == null)
			return;
		
		Option option = listView.getSelectionModel().getSelectedItem();
		if (option != null) {
			map.remove(option.getOptionName());
		}
	}
	
	@FXML
	public void editAction(ActionEvent event) {
		if (model.getContent() == null)
			return;
		
		Option option = listView.getSelectionModel().getSelectedItem();
		if (option != null) {
			Editor editor = option.getBestEditor(true);
			if (editor != null) {
				editor.setReadOnly(this.editor.isReadOnly());
				new EditorWindow(editor).startEdit(option);
			}
		}
	}

	@Override
	public void setEditor(Editor editor) {
		this.editor = editor;
	}

	@Override
	public Editor getEditor() {
		return editor;
	}
	
}
