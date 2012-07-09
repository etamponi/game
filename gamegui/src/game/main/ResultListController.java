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
package game.main;

import game.configuration.Configurable;
import game.core.Experiment;
import game.core.Metric;
import game.editorsystem.EditorWindow;
import game.editorsystem.Option;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

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
	
	public void addCompletedExperiment(Experiment e) {
		TreeItem expItem = new TreeItem(e);
		for(Metric eva: e.results.getList(Metric.class)) {
			if (!eva.isReady())
				continue;
			TreeItem evaItem = new TreeItem(eva);
			expItem.getChildren().add(evaItem);
		}
		if (!expItem.getChildren().isEmpty())
			resultsView.getRoot().getChildren().add(expItem);
	}
	
	@FXML
	public void onLoad(ActionEvent event) {
		FileChooser chooser = new FileChooser();
		chooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		chooser.setTitle("Load completed experiment");
		File file = chooser.showOpenDialog(resultsView.getScene().getWindow());
		if (file != null)
			addCompletedExperiment((Experiment)Configurable.createFromConfiguration(file));
	}
	
	@FXML
	public void onShow(ActionEvent event) {
		TreeItem selected = (TreeItem)resultsView.getSelectionModel().getSelectedItem();
		if (selected.getValue() instanceof Metric) {
			TextViewer viewer = new TextViewer((Metric)selected.getValue());
			viewer.show();
		}
		if (selected.getValue() instanceof Experiment) {
			Option option = new Option(selected.getValue());
			new EditorWindow(option.getBestEditor(true)).startEdit(option);
		}
	}
	
	@FXML
	public void onRemove(ActionEvent event) {
		TreeItem selected = (TreeItem)resultsView.getSelectionModel().getSelectedItem();
		if (selected.getValue() instanceof Experiment) {
			resultsView.getRoot().getChildren().remove(selected);
		}
	}
	
	@FXML
	public void onClear(ActionEvent event) {
		resultsView.getRoot().getChildren().clear();
	}

}
