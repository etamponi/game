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
package game.plugins.editors.graph;

import game.configuration.Configurable;
import game.configuration.Configurable.Change;
import game.core.Block;
import game.core.Graph;
import game.editorsystem.Editor;
import game.editorsystem.EditorController;
import game.editorsystem.Option;
import game.main.Settings;
import game.plugins.PluginManager;
import game.plugins.editors.ConfigurableEditor;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;

public class GraphEditorController implements EditorController {

	private Graph graph;
	
	@FXML
	private AnchorPane root;
	@FXML
	private AnchorPane leftSide;
	@FXML
	private AnchorPane confPane;
	@FXML
	private FlowPane classifiersPane;
	@FXML
	private FlowPane inputEncodersPane;
	@FXML
	private FlowPane pipesPane;
	@FXML
	private Slider zoom;

	private GraphPane graphPane;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		graphPane = new GraphPane(50, 50, getClass().getResource("background.png").toExternalForm());
		leftSide.getChildren().add(graphPane);
		AnchorPane.setTopAnchor(graphPane, 0.0);
		AnchorPane.setLeftAnchor(graphPane, 0.0);
		AnchorPane.setRightAnchor(graphPane, 0.0);
		AnchorPane.setBottomAnchor(graphPane, 30.0);

		root.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (graphPane.isConnectionInProgress() && event.getCode() == KeyCode.ESCAPE)
					graphPane.endConnection();
			}
		});
		
		root.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				onZoom(null);
			}
		});
		
		root.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				if (event.getDragboard().hasContent(BlockNode.BLOCKDATA)) {
					event.acceptTransferModes(TransferMode.ANY);
				}
			}
		});
		
		root.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				AnchorPane graphRoot = graphPane.getContentPane();
				for (Node child: new LinkedList<>(graphRoot.getChildren())) {
					if (child instanceof Connection) {
						if (((Connection)child).invalid()) {
							graphRoot.getChildren().remove(child);
							((Connection)child).removeFromModel();
						}
					}
				}
			}
		});
	}

	@Override
	public void setModel(Option model) {
		if (model != null) {
			graph = model.getContent();
			graphPane.setGraph(graph);
		} else {
			graph = null;
			graphPane.setGraph(null);
		}
	}

	@Override
	public void connectView() {
		graphPane.parseGraph();
		fillPools();
		connectConfRoot();
	}

	@Override
	public void updateView(Change change) {
		if (change.getPath().matches(".+\\.template.*"))
			fillPools();
		if (change.getPath().matches(".+\\.classifiers")
				|| change.getPath().matches(".+\\.inputEncoders")
				|| change.getPath().matches(".+\\.pipes")
				|| change.getPath().matches(".+\\.outputClassifier"))
			graphPane.parseGraph();
	}
	
	private void fillPools() {
		fillPool(classifiersPane, "classifiers");
		fillPool(inputEncodersPane, "inputEncoders");
		fillPool(pipesPane, "pipes");
	}
	
	private static class GraphConfigurationEditor extends ConfigurableEditor {
		
		public GraphConfigurationEditor() {
			addHiddenOption("classifiers");
			addHiddenOption("inputEncoders");
			addHiddenOption("pipes");
			addHiddenOption("outputClassifier");
		}
		
	}
	
	private void connectConfRoot() {
		confPane.getChildren().clear();
		Editor confEditor = new GraphConfigurationEditor();
		confEditor.setModel(new Option(graph));
		confPane.getChildren().add(confEditor.getView());
	}
	
	private void fillPool(FlowPane pool, String optionName) {
		pool.getChildren().clear();
		PluginManager manager = Settings.getInstance().getPluginManager();
		Configurable list = graph.getOption(optionName);
		if (list == null)
			return;
		Set<Block> blocks = list.getCompatibleOptionInstances("*", manager);
		for (Block block: blocks) {
			int index = 0;
			while (index < pool.getChildren().size()
					&& ((BlockNode)pool.getChildren().get(index)).getBlock().getClass().getSimpleName().compareTo(block.getClass().getSimpleName()) < 0)
				index++;
			pool.getChildren().add(index, new BlockNode(block, true, graphPane));
		}
	}

	@FXML
	void onZoom(Event event) {
		double value = zoom.getValue()/100;
		
		graphPane.setZoomLevel(value);
	}

}
