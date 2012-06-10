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
import game.core.blocks.Classifier;
import game.core.blocks.Encoder;
import game.editorsystem.Editor;
import game.editorsystem.EditorController;
import game.editorsystem.Option;
import game.main.Settings;
import game.plugins.PluginManager;
import game.utils.Utils;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class GraphEditorController implements EditorController, Observer {
	/*
	private static final ImageCursor TRASH = new ImageCursor(new Image(GraphEditorController.class.getResourceAsStream("trash.gif")));
	private static final ImageCursor ADD = new ImageCursor(new Image(GraphEditorController.class.getResourceAsStream("add.png")));
	*/
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
	
	private ZoomablePane graphPane;
	
	private AnchorPane graphRoot;

	private Polygon outputBlock = null;
	private boolean connectionInProgress = false;
	private Map<BlockNode, EventHandler> previousHandlers = new HashMap<>();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		graphPane = new ZoomablePane(2000, 2000);
		leftSide.getChildren().add(graphPane);
		AnchorPane.setTopAnchor(graphPane, 0.0);
		AnchorPane.setLeftAnchor(graphPane, 0.0);
		AnchorPane.setRightAnchor(graphPane, 0.0);
		AnchorPane.setBottomAnchor(graphPane, 30.0);
		graphRoot = graphPane.getContentPane();
		
		root.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (connectionInProgress && event.getCode() == KeyCode.ESCAPE)
					endConnection();
			}
		});
		
		graphRoot.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				HandlePosition handle = (HandlePosition)event.getDragboard().getContent(BlockNode.BLOCKDATA);
				if (handle != null) {
					event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
					
					BlockNode node = Settings.getInstance().getDragging();
					node.setPosition(handle, event.getX(), event.getY());
					node.setDragging(true);
				}
				
				event.consume();
			}
		});
		
		graphRoot.setOnDragEntered(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				if (event.getDragboard().hasContent(BlockNode.BLOCKDATA)) {
					BlockNode node = Settings.getInstance().getDragging();
					
					if (node.getWrapper() == null && !graphRoot.getChildren().contains(node))
						graphRoot.getChildren().add(node);
					if (node.getWrapper() != null && !graphRoot.getChildren().contains(node.getWrapper()))
						graphRoot.getChildren().add(node.getWrapper());
					
					updateConnections(node.getWrapper(), false);
				}
				
				event.consume();
			}
		});
		
		graphRoot.setOnDragExited(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				if (event.getDragboard().hasContent(BlockNode.BLOCKDATA)) {
					BlockNode node = Settings.getInstance().getDragging();
					if (node != null) {
						if (node.getBlock() instanceof Classifier) {
							graph.setOption("classifiers.remove", node.getBlock());
							if (graph.outputClassifier == node.getBlock())
								graph.setOption("outputClassifier", null);
						}
						if (node.getBlock() instanceof Encoder)
							graph.setOption("inputEncoders.remove", node.getBlock());
						
						if (node.getWrapper() != null)
							graphRoot.getChildren().remove(node.getWrapper());
						else
							graphRoot.getChildren().remove(node);
						
						updateConnections(node.getWrapper(), true);
					}
				}
				
				event.consume();
			}
		});
		
		graphRoot.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				if (event.getDragboard().hasContent(BlockNode.BLOCKDATA)) {
					BlockNode node = Settings.getInstance().getDragging();
					node.setDragging(false);
					
					if (node.getWrapper() == null)
						wrapNode(node);
					
					Block block = node.getBlock();
					if (block instanceof Classifier && !((List)graph.getOption("classifiers")).contains(block))
						graph.setOption("classifiers.add", block);
					if (block instanceof Encoder && !((List)graph.getOption("inputEncoders")).contains(block))
						graph.setOption("inputEncoders.add", block);
					
					Settings.getInstance().setDragging(null);
					event.setDropCompleted(true);
				} else {
					event.setDropCompleted(false);
				}
				
				event.consume();
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
				for (Node child: new LinkedList<>(graphRoot.getChildren())) {
					if (child instanceof Connection) {
						if (((Connection)child).invalid())
							graphRoot.getChildren().remove(child);
					}
				}
			}
		});
	}
	
	private void updateConnections(HBox box, boolean hide) {
		for (Node child: graphRoot.getChildren()) {
			if (child instanceof Connection) {
				if (((Connection)child).relativeTo(box)) {
					if (hide)
						child.setOpacity(0);
					else
						child.setOpacity(1);
				}
			}
		}
	}
	
	private HBox wrapNode(final BlockNode node) {
		final HBox wrapper = new HBox();
		wrapper.setAlignment(Pos.CENTER);
		
		Polygon in = new Polygon();
		in.getPoints().addAll(new Double[]{
			 0.0,  0.0,
			20.0, 10.0,
			 0.0, 20.0
		});
		in.setFill(Color.GREEN);
		final Polygon out = new Polygon();
		out.getPoints().addAll(new Double[]{
			 0.0,  0.0,
			20.0, 10.0,
			 0.0, 20.0
		});
		out.setFill(Color.GREEN);
		if (graph.outputClassifier != node.getBlock())
			out.setOpacity(0);
		else
			outputBlock = out;
		node.setWrapper(wrapper);
		wrapper.getChildren().addAll(in, node, out);
		wrapper.setLayoutX(node.getLayoutX() - 20.0);
		wrapper.setLayoutY(node.getLayoutY());
		graphRoot.getChildren().remove(node);
		graphRoot.getChildren().add(wrapper);

		wrapper.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				graphPane.setPannable(false);
			}
		});
		wrapper.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				graphPane.setPannable(true);
			}
		});
		
		if (node.getBlock() instanceof Encoder) {
			in.setOpacity(0);
		} else {
			in.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					if (!connectionInProgress && event.getClickCount() > 1)
						startConnection(wrapper);
				}
			});
		}
		
		if (node.getBlock() instanceof Classifier) {
			node.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					if (event.getButton() == MouseButton.SECONDARY) {
						if (node.getBlock() == graph.outputClassifier) {
							graph.setOption("outputClassifier", null);
							out.setOpacity(0);
						} else {
							if (outputBlock != null)
								outputBlock.setOpacity(0);
							graph.setOption("outputClassifier", node.getBlock());
							out.setOpacity(1);
							outputBlock = out;
						}
					}
				}
			});
		}
		
		return wrapper;
	}
	
	private void startConnection(HBox wrapper) {
		previousHandlers.clear();
		
		final BlockNode node = (BlockNode)wrapper.getChildren().get(1);
		connectionInProgress = true;
		for (Node child: graphRoot.getChildren()) {
			if (!(child instanceof HBox))
				continue;
			
			if (child == wrapper)
				continue;
			final BlockNode other = (BlockNode)((HBox)child).getChildren().get(1);
			
			if (node.getBlock().parents.contains(other.getBlock()))
				other.setStyle("-fx-border-style: solid; -fx-border-size: 3px; -fx-border-color:red");
			else
				other.setStyle("-fx-border-style: solid; -fx-border-size: 3px; -fx-border-color:green");
			
			previousHandlers.put(other, other.getOnMouseClicked());
			other.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					if (node.getBlock().parents.contains(other.getBlock())) {
						node.getBlock().setOption("parents.remove", other.getBlock());
						removeConnection(other.getWrapper(), node.getWrapper());
					} else {
						node.getBlock().setOption("parents.add", other.getBlock());
						addConnection(other.getWrapper(), node.getWrapper());
					}
					endConnection();
				}
			});
		}
	}
	
	private void addConnection(HBox from, HBox to) {
		final Connection connection = new Connection(graphRoot, from, to);
		graphRoot.getChildren().add(connection);
	}
	
	private void removeConnection(HBox from, HBox to) {
		for (Node child: graphRoot.getChildren()) {
			if (child instanceof Connection) {
				if (((Connection)child).matches(from, to)) {
					graphRoot.getChildren().remove(child);
					return;
				}
			}
		}
	}
	
	private void endConnection() {
		connectionInProgress = false;
		for (Node child: graphRoot.getChildren()) {
			if (child instanceof HBox) {
				BlockNode node = (BlockNode)((HBox)child).getChildren().get(1); 
				node.setStyle("-fx-border-style: solid; -fx-border-color:gray");
				if (previousHandlers.containsKey(node))
					node.setOnMouseClicked(previousHandlers.get(node));
			}
		}
		
		previousHandlers.clear();
	}

	@Override
	public void setModel(Option model) {
		if (graph != null)
			graph.deleteObserver(this);
		
		if (model != null) {
			graph = model.getContent();
			graph.addObserver(this);
		} else {
			graph = null;
		}
	}

	@Override
	public void connectView() {
		connectConfRoot();
		fillPools();
		parseGraph();
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
			pool.getChildren().add(index, new BlockNode(block, true));
		}
	}
	
	private void parseGraph() {
		Set<Block> seen = new HashSet<>();
		
		if (graph.outputClassifier != null) {
			int level = countLevels(graph.outputClassifier, new HashSet<Block>());
			
			List<HBox> currentWrappers = new LinkedList<>();
			List<Block> currentBlocks = new LinkedList<>();
			currentBlocks.add(graph.outputClassifier);
			List<HBox> previousWrappers = new LinkedList<>();
			List<Block> previousBlocks = new LinkedList<>();
			
			while (level > 0) {
				for(int i = 0; i < currentBlocks.size(); i++) {
					Block c = currentBlocks.get(i);
					if (seen.contains(c)) {
						currentBlocks.remove(i);
						i--;
						continue;
					}
					seen.add(c);
					if (c instanceof Classifier && !graph.classifiers.contains(c))
						graph.setOption("classifiers.add", c);
					if (c instanceof Encoder && !graph.inputEncoders.contains(c))
						graph.setOption("inputEncoders.add", c);
					
					BlockNode node = new BlockNode(c, false);
					graphRoot.getChildren().add(node);
					node.setPosition(new HandlePosition(0, 0), 100*level+20, 100*(i+2));
					HBox wrapper = wrapNode(node);
					for (Block p: previousBlocks) {
						if (p.parents.contains(c)) {
							graphRoot.getChildren().add(
									new Connection(graphRoot, wrapper, previousWrappers.get(previousBlocks.indexOf(p))));
						}
					}
					currentWrappers.add(wrapper);
				}
				previousBlocks.clear();
				previousBlocks.addAll(currentBlocks);
				previousWrappers.clear();
				previousWrappers.addAll(currentWrappers);
				currentWrappers.clear();
				currentBlocks.clear();
				for(Block b: previousBlocks)
					currentBlocks.addAll(b.parents);
				level--;
			}
		}
		
		int count = addUnconnectedBlocks(graph.classifiers, seen, 1);
		addUnconnectedBlocks(graph.inputEncoders, seen, count);
	}
	
	private int addUnconnectedBlocks(List<Block> blocks, Set<Block> seen, int startAt) {
		int count = startAt;
		for (Block b: blocks) {
			if (seen.contains(b))
				continue;
			seen.add(b);
			
			BlockNode node = new BlockNode(b, false);
			graphRoot.getChildren().add(node);
			node.setPosition(new HandlePosition(0, 0), 100*count+20, 0);
			wrapNode(node);
			count++;
		}
		return count;
	}
	
	private int countLevels(Block current, Set<Block> seen) {
		if (seen.contains(current))
			return 0;
		seen.add(current);
		if (current.parents.isEmpty())
			return 1;
		else {
			int max = 0;
			for (Block parent: current.parents.getList(Block.class)) {
				int count = countLevels(parent, seen);
				if (count > max)
					max = count;
			}
			return 1+max;
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
