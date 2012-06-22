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
package game.plugins.editors.configurablelist;

import game.configuration.Configurable.Change;
import game.configuration.ConfigurableList;
import game.editorsystem.Editor;
import game.editorsystem.EditorController;
import game.editorsystem.EditorWindow;
import game.editorsystem.Option;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class ConfigurableListEditorController implements EditorController {
	
	private Option model;
	
	@FXML
	private ListView<Option> listView;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
	
	@Override
	public void setModel(Option model) {
		this.model = model;
	}

	@Override
	public void connectView() {
		listView.getItems().clear();
		ConfigurableList list = model.getContent();
		if (list == null)
			return;
		
		for (int i = 0; i < list.size(); i++) {
			Option option = new Option(list, String.valueOf(i));
			listView.getItems().add(option);
		}
	}

	@Override
	public void updateView(Change change) {
		connectView();
	}
	
	@FXML
	public void addAction(ActionEvent event) {
		ConfigurableList list = model.getContent();
		if (list == null)
			return;
		
		list.add(null);
	}
	
	@FXML
	public void removeAction(ActionEvent event) {
		ConfigurableList list = model.getContent();
		if (list == null)
			return;
		
		int index = listView.getSelectionModel().getSelectedIndex();
		if (index >= 0)
			list.remove(index);
	}
	
	@FXML
	public void editAction(ActionEvent event) {
		ConfigurableList list = model.getContent();
		if (list == null)
			return;
		
		int index = listView.getSelectionModel().getSelectedIndex();
		if (index >= 0) {
			Editor editor = listView.getItems().get(index).getBestEditor();
			editor.setModel(listView.getItems().get(index));
			new EditorWindow(editor).show();
		}
	}
	
}
