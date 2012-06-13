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
import game.utils.Utils;

import java.net.URL;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class GraphEditorController implements EditorController, Observer {

	private Graph graph;
	
	@FXML
	private AnchorPane root;
	@FXML
	private AnchorPane leftSide;
	@FXML
	private GridPane confPane;
	@FXML
	private FlowPane classifiersPane;
	@FXML
	private FlowPane inputEncodersPane;
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
						if (((Connection)child).invalid())
							graphRoot.getChildren().remove(child);
					}
				}
			}
		});
	}

	@Override
	public void setModel(Option model) {
		if (graph != null)
			graph.deleteObserver(this);
		
		if (model != null) {
			graph = model.getContent();
			graph.addObserver(this);
			graphPane.setGraph(graph);
		} else {
			graph = null;
			graphPane.setGraph(null);
		}
	}

	@Override
	public void connectView() {
		connectConfRoot();
		fillPools();
		graphPane.parseGraph();
	}

	@Override
	public void updateView() {
		// The view is updated locally, not as a whole.
	}
	
	private void fillPools() {
		fillPool(classifiersPane, "classifiers");
		fillPool(inputEncodersPane, "inputEncoders");
	}
	
	private void connectConfRoot() {
		confPane.getChildren().clear();
		if (graph != null) {
			addConfPaneRow("name", 0);
			addConfPaneRow("template", 1);
			addConfPaneRow("decoder", 2);
		}
	}
	
	private void addConfPaneRow(String optionName, int rowIndex) {
		Option option = new Option(graph, optionName);
		Label label = new Label(optionName+": ");
		Editor editor = option.getBestEditor();
		try {
			if (option.getContent() == null && Utils.isConcrete(option.getType()))
				option.setContent(option.getType().newInstance());
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		editor.setModel(option);
		
		if (option.isBound())
			editor.getView().setDisable(true);
		
		confPane.addRow(rowIndex, label, editor.getView());
		GridPane.setValignment(label, VPos.TOP);
		GridPane.setHalignment(label, HPos.RIGHT);
		GridPane.setMargin(label, new Insets(5, 2, 2, 2));
		GridPane.setHgrow(editor.getView(), Priority.ALWAYS);
		GridPane.setMargin(editor.getView(), new Insets(2, 2, 2, 2));
	}
	
	private void fillPool(FlowPane pool, String optionName) {
		pool.getChildren().clear();
		PluginManager manager = Settings.getInstance().getPluginManager();
		Configurable list = graph.getOption(optionName);
		
		Set<Block> blocks = list.getCompatibleOptionInstances("*", manager);
		for (Block block: blocks) {
			int index = 0;
			while (index < pool.getChildren().size()
					&& ((BlockNode)pool.getChildren().get(index)).getBlock().getClass().getSimpleName().compareTo(block.getClass().getSimpleName()) < 0)
				index++;
			pool.getChildren().add(index, new BlockNode(block, true, graphPane));
		}
	}

	@Override
	public void update(Observable o, Object message) {
		if (message instanceof Change) {
			Change change = (Change)message;
			if (change.getPath().startsWith("template"))
				fillPools();
		}
	}
	
	@FXML
	void onZoom(Event event) {
		double value = zoom.getValue()/100;
		
		graphPane.setZoomLevel(value);
	}

}
