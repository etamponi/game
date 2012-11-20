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

import game.core.Block;
import game.core.blocks.PredictionGraph;
import game.editorsystem.EditorWindow;
import game.editorsystem.PropertyEditor;
import game.plugins.editors.graph.OuterGraphEditor.GraphEditor;
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

import com.ios.IObject;
import com.ios.Property;
import com.ios.listeners.SubPathListener;
import com.ios.triggers.SimpleTrigger;

public class BlockNode extends VBox {
	
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
	
	BlockParent blockParent;
	
	class BlockParent extends IObject {
		
		public Block block;
		
		public BlockParent(boolean isTemplate, final Block b) {
			setContent("block", b);
			
			if (!isTemplate) {
				addTrigger(new SimpleTrigger(new SubPathListener(new Property(this, "block"))) {
					private BlockParent parent = BlockParent.this;
					@Override
					public void action(Property changedPath) {
						if (parent.block != null)
							updateView(block);
					}
				});
			}
		}
		
	}
	
	public void updateView(Block block) {
		blockName.setText(block.name);
		pane.fixPosition(this);
		status.setImage(block.getErrors().isEmpty() ? STATUSOK : STATUSERRORS);
	}

	public BlockNode(final Block b, boolean isTpl, GraphPane p) {
		this.isTemplate = isTpl;
		this.pane = p;
		
		if (isTemplate)
			b.setContent("name", b.getClass().getSimpleName());
		
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
				pane.setDragging( isTemplate ? new BlockNode((Block)b.copy(), false, pane) : BlockNode.this);
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
			status.setImage(b.getErrors().isEmpty() ? STATUSOK : STATUSERRORS);
			
			setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					if (event.getClickCount() > 1) {
						PropertyEditor editor;
						if (PredictionGraph.class.isAssignableFrom(b.getClass()))
							editor = new GraphEditor();
						else
							editor = PropertyEditor.getBestEditor(b.getClass());
						editor.setReadOnly(pane.isReadOnly());
						new EditorWindow(editor).startEdit(new Property(blockParent, "block"));
					}
				}
			});
			
		}
		
		this.blockParent = new BlockParent(isTemplate, b);
	}
	
	public void destroy() {
		pane.removeBlock(blockParent.block);
		blockParent.detach();
	}
	
	public Block getBlock() {
		return blockParent.block;
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
	
}
