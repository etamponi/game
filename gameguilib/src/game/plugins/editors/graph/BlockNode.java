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
package game.plugins.editors.graph;


import game.configuration.Change;
import game.core.Block;
import game.core.blocks.PredictionGraph;
import game.editorsystem.EditorWindow;
import game.editorsystem.Option;
import game.editorsystem.Editor;
import game.plugins.editors.graph.OuterGraphEditor.GraphEditor;

import java.util.Observable;
import java.util.Observer;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class BlockNode extends VBox implements Observer {
	
	private static final double BLOCKWIDTH = 80;
	private static final double BLOCKHEIGHT = 42;

	private static final Image STATUSOK = new Image(BlockNode.class.getResourceAsStream("blockOk.png"));
	private static final Image STATUSERRORS = new Image(BlockNode.class.getResourceAsStream("blockErrors.png"));
	
	public static final DataFormat BLOCKDATA = new DataFormat("game/block");
	
	private boolean isTemplate;
	
	private ImageView status = new ImageView();
	
	private Text blockName;
	
	private HBox wrapper;
	
	private GraphPane pane;
	
	private Option model;

	public BlockNode(final Block b, boolean isTpl, GraphPane p) { 
		this.model = new Option(b);
		this.isTemplate = isTpl;
		this.pane = p;
		
		if (isTemplate)
			b.setOption("name", b.getClass().getSimpleName());
		
		setStyle("-fx-border-style: solid; -fx-border-color:gray;");
		setPadding(new Insets(5));
		
		AnchorPane imagePane = new AnchorPane();
		Rectangle rect = new Rectangle(BLOCKWIDTH, BLOCKHEIGHT);
		rect.setFill(Color.INDIGO);
		imagePane.getChildren().addAll(rect, status);
		
		blockName = new Text(b.name);
		blockName.setTextAlignment(TextAlignment.CENTER);
		blockName.setWrappingWidth(BLOCKWIDTH);
		
		getChildren().addAll(imagePane, blockName);
		
		setOnDragDetected(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (pane.isReadOnly())
					return;
				
				Dragboard db = startDragAndDrop(isTemplate ? TransferMode.COPY : TransferMode.MOVE);
				
				ClipboardContent content = new ClipboardContent();
				pane.setDragging( isTemplate ? new BlockNode((Block)b.cloneConfiguration(), false, pane) : BlockNode.this);
				content.put(BLOCKDATA, new HandlePosition(event.getX(), event.getY()));
				
				db.setContent(content);
				
				event.consume();
			}
		});
		
		setOnDragDone(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				pane.setDragging(null);
				event.consume();
			}
		});
	
		if (!isTemplate) {
			status.setImage(b.getConfigurationErrors().isEmpty() ? STATUSOK : STATUSERRORS);
			
			model.getOwner().addObserver(this);
			
			setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					if (event.getClickCount() > 1) {
						Editor editor;
						if (PredictionGraph.class.isAssignableFrom(model.getType(true)))
							editor = new GraphEditor();
						else
							editor = model.getBestEditor(true);
						editor.setReadOnly(pane.isReadOnly());
						new EditorWindow(editor).startEdit(model);
					}
				}
			});
		}
	}
	
	public void destroy() {
		pane.removeBlock((Block)model.getContent());
		disconnect();
	}
	
	public void disconnect() {
		model.getOwner().deleteObserver(this);
		model.setContent(null);
	}
	
	public Block getBlock() {
		return model.getContent();
	}
	
	public void setWrapper(HBox wrapper) {
		this.wrapper = wrapper;
	}
	
	public HBox getWrapper() {
		return wrapper;
	}
	
	public void setPosition(HandlePosition handle, double left, double top) {
		if (wrapper == null) {
			setLayoutX(left-handle.x);
			setLayoutY(top-handle.y);
		} else {
			wrapper.setLayoutX(left-handle.x);
			wrapper.setLayoutY(top-handle.y);
		}
	}
	
	public void setDragging(boolean dragging) {
		if (dragging)
			(wrapper != null ? wrapper : this).setOpacity(0.3);
		else
			(wrapper != null ? wrapper : this).setOpacity(1.0);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof Change) {
			Change change = (Change)arg;
			if (change.getPath().equals("content.name")) {
				if (!blockName.getText().equals(getBlock().name)) {
					blockName.setText(getBlock().name);
					pane.fixPosition(BlockNode.this);
				}
			}
			
			status.setImage(getBlock().getConfigurationErrors().isEmpty() ? STATUSOK : STATUSERRORS);
		}
	}
	
}
