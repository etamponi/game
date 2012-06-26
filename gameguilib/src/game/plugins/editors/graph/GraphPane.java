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
import game.core.Graph;
import game.core.blocks.Classifier;
import game.core.blocks.Encoder;
import game.core.blocks.Pipe;
import game.main.Settings;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Scale;

public class GraphPane extends ScrollPane {

	private Graph graph;
	
	private AnchorPane parent = new AnchorPane();
	
	private AnchorPane content = new AnchorPane();
	
	private Polygon outputBlock = null;
	private boolean connectionInProgress = false;
	private Map<BlockNode, EventHandler> previousHandlers = new HashMap<>();
	
	private double minimum = 0.1;
	private double level = 1;

	private double cellHeight;
	private double cellWidth;
	
	public GraphPane(int hcells, int vcells, String cellImage) {
		
		Image img = new Image(cellImage);
		this.cellWidth = img.getWidth();
		this.cellHeight = img.getHeight();
		
		setHbarPolicy(ScrollBarPolicy.ALWAYS);
		setVbarPolicy(ScrollBarPolicy.ALWAYS);
		
		double width = hcells*cellWidth;
		double height = vcells*cellHeight;

		content.setPrefSize(width, height);
		content.setMinSize(width, height);
		content.setMaxSize(width, height);
		
		content.setStyle("-fx-background-image: url('" + cellImage + "');" + 
		           		 "-fx-background-position: 0 0;" +
		           		 "-fx-background-repeat: repeat;");
		
		parent.getChildren().add(content);
		setPannable(true);
		setContent(parent);
		updateContentPane();
		
		content.setOnDragOver(new EventHandler<DragEvent>() {
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
		
		content.setOnDragEntered(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				if (event.getDragboard().hasContent(BlockNode.BLOCKDATA)) {
					BlockNode node = Settings.getInstance().getDragging();
					
					if (node.getWrapper() == null && !content.getChildren().contains(node))
						content.getChildren().add(node);
					if (node.getWrapper() != null && !content.getChildren().contains(node.getWrapper()))
						content.getChildren().add(node.getWrapper());
					
					updateConnections(node, false);
				}
				
				event.consume();
			}
		});
		
		content.setOnDragExited(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				if (event.getDragboard().hasContent(BlockNode.BLOCKDATA)) {
					BlockNode node = Settings.getInstance().getDragging();
					if (node != null) {
						if (node.getWrapper() != null)
							content.getChildren().remove(node.getWrapper());
						else
							content.getChildren().remove(node);
						
						updateConnections(node, true);
					}
				}
				
				event.consume();
			}
		});
		
		content.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				if (event.getDragboard().hasContent(BlockNode.BLOCKDATA)) {
					BlockNode node = Settings.getInstance().getDragging();
					node.setDragging(false);
					
					if (node.getWrapper() == null)
						wrapNode(node);
					fixPosition(node);
					
					Block block = node.getBlock();
					if (block instanceof Classifier && !graph.classifiers.contains(block))
						graph.setOption("classifiers.add", block);
					if (block instanceof Encoder && !graph.inputEncoders.contains(block))
						graph.setOption("inputEncoders.add", block);
					if (block instanceof Pipe && !graph.pipes.contains(block))
						graph.setOption("pipes.add", block);
					
					Settings.getInstance().setDragging(null);
					event.setDropCompleted(true);
				} else {
					event.setDropCompleted(false);
				}
				
				event.consume();
			}
		});
	}

	public boolean isConnectionInProgress() {
		return connectionInProgress;
	}
	
	public AnchorPane getContentPane() {
		return content;
	}
	
	public void setGraph(Graph g) {
		this.graph = g;
	}
	
	public void setZoomLevel(double level) {
		assert(level >= minimum && level <= 1);
		this.level = level;
		updateContentPane();
	}
	
	public void endConnection() {
		connectionInProgress = false;
		for (Node child: content.getChildren()) {
			if (child instanceof HBox) {
				BlockNode node = (BlockNode)((HBox)child).getChildren().get(1); 
				node.setStyle("-fx-border-style: solid; -fx-border-color:gray");
				if (previousHandlers.containsKey(node))
					node.setOnMouseClicked(previousHandlers.get(node));
			}
		}
		
		previousHandlers.clear();
	}
	
	public void parseGraph() {
		disconnectBlockNodes();
		content.getChildren().clear();
		
		List<Block> all = new LinkedList<>();
		all.addAll(graph.inputEncoders);
		all.addAll(graph.pipes);
		all.addAll(graph.classifiers);
		
		Map<Integer, Integer> countPerLevel = new HashMap<>();
		List<BlockNode> allNodes = new LinkedList<>();
		
		for(Block b: all) {
			BlockNode node = new BlockNode(b, false, this);
			double left, top;
			if (b.position.isValid()) {
				left = cellWidth*b.position.x;
				top = cellHeight*b.position.y;
			} else {
				int level = levelOf(b, new HashSet<Block>());
				if (!countPerLevel.containsKey(level))
					countPerLevel.put(level, 1);
				int count = countPerLevel.get(level);
				
				left = cellWidth*level;
				top = cellHeight*count;

				countPerLevel.put(level, count+1);
			}
			content.getChildren().add(node);
			node.setPosition(new HandlePosition(0, 0), left, top);
			wrapNode(node);
			fixPosition(node);
			
			allNodes.add(node);
		}
		
		for(int i = 0; i < allNodes.size(); i++) {
			for(int j = 0; j < allNodes.size(); j++) {
				if (i == j)
					continue;
				BlockNode A = allNodes.get(i);
				BlockNode B = allNodes.get(j);
				
				if (A.getBlock().getParents().contains(B.getBlock()))
					addConnection(B, A);
			}
		}
	}
	
	private void disconnectBlockNodes() {
		for (Node child: content.getChildren()) {
			if (child instanceof HBox)
				((BlockNode)((HBox)child).getChildren().get(1)).disconnect();
		}
	}
	
	public void fixPosition(BlockNode node) {
		HBox wrapper = node.getWrapper();
		wrapper.getParent().layout();
		
		double x = wrapper.getLayoutX()+wrapper.getWidth()/2;
		double y = wrapper.getLayoutY()+wrapper.getHeight()/2;
		
		int hcell = getNearestCell(x, cellWidth);
		int vcell = getNearestCell(y, cellHeight);

		node.getBlock().position.x = hcell;
		node.getBlock().position.y = vcell;

		int offsetX = (int)(cellWidth - wrapper.getWidth())/2;
		int offsetY = (int)(cellHeight - wrapper.getHeight())/2;
		
		node.setPosition(new HandlePosition(0, 0), hcell*cellWidth+offsetX, vcell*cellHeight+offsetY);
	}
	
	private int getNearestCell(double pos, double cellSize) {
		return (int)(pos/cellSize);
	}
	
	private void updateContentPane() {
		content.getTransforms().clear();
		content.getTransforms().add(new Scale(level, level));
		parent.setPrefSize(content.getWidth()*level, content.getHeight()*level);
		parent.setMinSize(content.getWidth()*level, content.getHeight()*level);
		parent.setMaxSize(content.getWidth()*level, content.getHeight()*level);
	}
	
	private void updateConnections(BlockNode node, boolean hide) {
		for (Node child: content.getChildren()) {
			if (child instanceof Connection) {
				if (((Connection)child).relativeTo(node)) {
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
		wrapper.setLayoutX(node.getLayoutX());
		wrapper.setLayoutY(node.getLayoutY());
		content.getChildren().remove(node);
		content.getChildren().add(wrapper);

		wrapper.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				setPannable(false);
			}
		});
		wrapper.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				setPannable(true);
			}
		});
		
		if (!node.getBlock().acceptsNewParents()) {
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
		for (Node child: content.getChildren()) {
			if (!(child instanceof HBox))
				continue;
			
			if (child == wrapper)
				continue;
			final BlockNode other = (BlockNode)((HBox)child).getChildren().get(1);
			
			if (node.getBlock().getParents().contains(other.getBlock()))
				other.setStyle("-fx-border-style: solid; -fx-border-color:red");
			else
				other.setStyle("-fx-border-style: solid; -fx-border-color:green");
			
			previousHandlers.put(other, other.getOnMouseClicked());
			other.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					if (node.getBlock().getParents().contains(other.getBlock())) {
						node.getBlock().setOption("parents.remove", other.getBlock());
						removeConnection(other, node);
					} else {
						node.getBlock().setOption("parents.add", other.getBlock());
						addConnection(other, node);
					}
					endConnection();
				}
			});
		}
	}
	
	private void addConnection(BlockNode from, BlockNode to) {
		final Connection connection = new Connection(content, from, to);
		content.getChildren().add(connection);
	}
	
	private void removeConnection(BlockNode from, BlockNode to) {
		for (Node child: content.getChildren()) {
			if (child instanceof Connection) {
				if (((Connection)child).matches(from, to)) {
					content.getChildren().remove(child);
					return;
				}
			}
		}
	}
	
	private int levelOf(Block current, Set<Block> seen) {
		if (seen.contains(current))
			return 0;
		seen.add(current);
		if (current.getParents().isEmpty())
			return 1;
		else {
			int max = 0;
			for (Block parent: current.getParents().getList(Block.class)) {
				int count = levelOf(parent, seen);
				if (count > max)
					max = count;
			}
			return 1+max;
		}
	}

	public void removeBlock(Block block) {
		if (block instanceof Classifier) {
			graph.setOption("classifiers.remove", block);
			if (graph.outputClassifier == block)
				graph.setOption("outputClassifier", null);
		}
		if (block instanceof Encoder)
			graph.setOption("inputEncoders.remove", block);
		if (block instanceof Pipe)
			graph.setOption("pipes.remove", block);
	}
	
}
