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

import game.core.Block;
import game.core.blocks.Graph;
import game.editorsystem.EditorController;
import game.editorsystem.PropertyEditor;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Slider;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;

import com.ios.IList;
import com.ios.Property;

public class GraphEditorController implements EditorController {

	private Property graphModel;
	
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
	@FXML
	private Accordion accordion;

	private GraphPane graphPane;
	
	private PropertyEditor confEditor = new GraphConfigurationEditor();

	private PropertyEditor editor;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		confPane.getChildren().add(confEditor.getView());
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
				if (event.getDragboard().hasContent(BlockNode.BLOCKDATA)) {
					BlockNode node = graphPane.getDragging();
					node.setDragging(false);
					
					AnchorPane graphRoot = graphPane.getContentPane();
					for (Node child: new LinkedList<>(graphRoot.getChildren())) {
						if (child instanceof Connection) {
							if (((Connection)child).isInvalid()) {
								graphRoot.getChildren().remove(child);
								((Connection)child).removeFromModel();
							}
						}
					}
					node.destroy();
				}
				
				event.setDropCompleted(true);
				
				event.consume();
			}
		});
	}

	@Override
	public void setModel(Property model) {
		this.graphModel = model;
		confEditor.connect(graphModel);
	}

	@Override
	public void updateView() {
		Graph graph = graphModel.getContent();
		if (graph != null) {
			graphPane.setGraph(graph);
			graphPane.parseGraph();
			
			if (!editor.isReadOnly())
				fillPools();
			if (editor.isReadOnly()) {
				accordion.setExpandedPane(accordion.getPanes().get(0));
				for(int i = 1; i < accordion.getPanes().size(); i++) {
					accordion.getPanes().get(i).setDisable(true);
				}
			} else {
				for(int i = 1; i < accordion.getPanes().size(); i++) {
					accordion.getPanes().get(i).setDisable(false);
				}
			}
			
//			graphPane.setDisable(editor.isReadOnly());
		}
	}

	private void fillPools() {
		fillPool(classifiersPane, "blocks");
	}
	
	private static class GraphConfigurationEditor extends BlockEditor {
		
		public GraphConfigurationEditor() {
			setHiddenOptions("blocks", "outputBlock");
		}
		
	}
	
	private void fillPool(FlowPane pool, String listOptionName) {
		pool.getChildren().clear();
		IList list = ((Graph)graphModel.getContent()).getContent(listOptionName);
		if (list == null)
			return;
		Set<Class> blocks = list.getValidContentTypes("*");
		for (Class<? extends Block> type: blocks) {
			try {
				Block block = type.newInstance();
				int index = 0;
				while (index < pool.getChildren().size()
						&& ((BlockNode)pool.getChildren().get(index)).getBlock().getClass().getSimpleName().compareTo(block.getClass().getSimpleName()) < 0)
					index++;
				pool.getChildren().add(index, new BlockNode(block, true, graphPane));
			} catch (InstantiationException | IllegalAccessException e) {
				System.out.println("Cannot create instance of class " + type);
			}
		}
	}

	@FXML
	void onZoom(Event event) {
		double value = zoom.getValue()/100;
		
		graphPane.setZoomLevel(value);
	}

	@Override
	protected void finalize() throws Throwable {
		confEditor.detach();
	}

	@Override
	public void setEditor(PropertyEditor editor) {
		this.editor = editor;
	}

	@Override
	public PropertyEditor getEditor() {
		return editor;
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		confEditor.setReadOnly(readOnly);
		graphPane.setReadOnly(readOnly);
	}

}
