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
package game.plugins.editors.list;

import game.configuration.ConfigurableList;
import game.editorsystem.Editor;
import game.editorsystem.EditorController;
import game.editorsystem.EditorWindow;
import game.editorsystem.Option;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

public class ListEditorController implements EditorController {
	
	private Option model;
	
	@FXML
	private ListView<Option> listView;
	@FXML
	private Button addButton;
	@FXML
	private Button removeButton;
	@FXML
	private Button editButton;
	@FXML
	private Button upButton;
	@FXML
	private Button downButton;

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
		List list = model.getContent();
		if (list == null)
			return;
		
		ObservableList<Option> items = FXCollections.<Option>observableArrayList();
		for (int i = 0; i < list.size(); i++) {
			items.add(list instanceof ConfigurableList ?
					  new Option((ConfigurableList)list, String.valueOf(i))
					: new Option(list.get(i)));
		}
		//listView.getSelectionModel().select(-1);
		listView.getSelectionModel().clearSelection();
		listView.setItems(items);
		
		boolean disable = editor.isReadOnly() || !(list instanceof ConfigurableList);
		addButton.setDisable(disable);
		removeButton.setDisable(disable);
		upButton.setDisable(disable);
		downButton.setDisable(disable);
		if (editor.isReadOnly())
			editButton.setText("View");
		else
			editButton.setText("Edit");
	}
	
	@FXML
	public void addAction(ActionEvent event) {
		ConfigurableList list = model.getContent();
		if (list == null)
			return;
		
		list.add(null);
		
		listView.getSelectionModel().select(list.size()-1);
		editAction(event);
	}
	
	@FXML
	public void removeAction(ActionEvent event) {
		ConfigurableList list = model.getContent();
		if (list == null)
			return;
		
		int index = listView.getSelectionModel().getSelectedIndex();
		if (index >= 0) {
			list.remove(index);
		}
	}
	
	@FXML
	public void editAction(ActionEvent event) {
		if (model.getContent() == null)
			return;
		
		int index = listView.getSelectionModel().getSelectedIndex();
		if (index >= 0) {
			Editor editor = listView.getItems().get(index).getBestEditor(true);
			if (editor != null) {
				editor.setReadOnly(this.editor.isReadOnly());
				new EditorWindow(editor).startEdit(listView.getItems().get(index));
			}
		}
	}
	
	@FXML
	public void upAction(ActionEvent event) {
		if (model.getContent() == null)
			return;
		
		ConfigurableList list = model.getContent();
		
		int index = listView.getSelectionModel().getSelectedIndex();
		if (index > 0) {
			Object prev = list.get(index-1);
			Object curr = list.get(index);
			list.set(index-1, curr);
			list.set(index, prev);
			
			listView.getSelectionModel().select(index-1);
		}
	}
	
	@FXML
	public void downAction(ActionEvent event) {
		if (model.getContent() == null)
			return;
		
		ConfigurableList list = model.getContent();
		
		int index = listView.getSelectionModel().getSelectedIndex();
		if (index < listView.getItems().size()-1) {
			Object next = list.get(index+1);
			Object curr = list.get(index);
			list.set(index+1, curr);
			list.set(index, next);
			
			listView.getSelectionModel().select(index+1);
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
