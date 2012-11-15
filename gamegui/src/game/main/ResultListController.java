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
package game.main;

import game.configuration.IObject;
import game.configuration.PluginManager;
import game.configuration.Property;
import game.configuration.constraints.CompatibleWith;
import game.core.Metric;
import game.core.Result;
import game.editorsystem.EditorWindow;
import game.editorsystem.PropertyEditor;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.FileChooser;

public class ResultListController implements Initializable {
	
	@FXML
	private TreeView resultsView;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		resultsView.setRoot(new TreeItem("Results"));
		resultsView.getRoot().setExpanded(true);
	}
	
	public void addResult(Result r) {
		TreeItem expItem = new TreeItem(r);
		
		Set<Class> metrics = PluginManager.getCompatibleImplementationsOf(Metric.class, new CompatibleWith(new Property(r, "experment")));
		
		for(Class<Metric> impl: metrics) {
			try {
				TreeItem evaItem = new TreeItem(impl.newInstance());
				expItem.getChildren().add(evaItem);
			} catch (InstantiationException | IllegalAccessException e) {
				System.err.println("Cannot instantiate instance of " + impl);
			}
		}
		if (!expItem.getChildren().isEmpty())
			resultsView.getRoot().getChildren().add(expItem);
	}
	
	@FXML
	public void onLoad(ActionEvent event) {
		FileChooser chooser = new FileChooser();
		chooser.setInitialDirectory(new File(Settings.RESULTSDIR));
		chooser.setTitle("Load result");
		File file = chooser.showOpenDialog(resultsView.getScene().getWindow());
		if (file != null)
			addResult((Result)IObject.load(file));
	}
	
	@FXML
	public void onShow(ActionEvent event) {
		TreeItem selected = (TreeItem)resultsView.getSelectionModel().getSelectedItem();
		if (selected != null) {
			if (selected.getValue() instanceof Metric) {
				Result r = (Result)selected.getParent().getValue();
				Metric m = (Metric)selected.getValue();
				m.setResult(r);
			}
			Property property = new Property(selected.getValue());
			PropertyEditor editor = PropertyEditor.getBestEditor(property.getContentType(true));
			editor.setReadOnly(true);
			new EditorWindow(editor, false).startEdit(property);
		}
	}
	
	@FXML
	public void onRemove(ActionEvent event) {
		TreeItem selected = (TreeItem)resultsView.getSelectionModel().getSelectedItem();
		if (selected != null && selected.getValue() instanceof Result) {
			resultsView.getRoot().getChildren().remove(selected);
		}
	}
	
	@FXML
	public void onClear(ActionEvent event) {
		resultsView.getRoot().getChildren().clear();
	}

}
