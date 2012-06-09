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
import game.editorsystem.Editor;
import game.editorsystem.EditorWindow;
import game.editorsystem.Option;
import game.main.Settings;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;

public class BlockNode extends VBox {
	
	public static final DataFormat BLOCKDATA = new DataFormat("game/block");
	
	private Block block;
	
	private boolean isTemplate;
	
	private Label label;

	public BlockNode(Block b, boolean isTpl) {
		this.block = b;
		this.isTemplate = isTpl;
		
		setStyle("-fx-border-style: solid; -fx-border-color:green;");
		setPadding(new Insets(5));
		
		Rectangle rect = new Rectangle(50, 50);
		rect.setFill(Color.RED);
		
		label = new Label(isTemplate ? block.getClass().getSimpleName() : (String)block.getOption("name"));
		label.setWrapText(true);
		label.setAlignment(Pos.CENTER);
		label.setTextAlignment(TextAlignment.CENTER);
		label.setPrefWidth(50.0);
		
		getChildren().addAll(rect, label);
		
		setOnDragDetected(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Dragboard db = startDragAndDrop(isTemplate ? TransferMode.COPY : TransferMode.MOVE);
				
				ClipboardContent content = new ClipboardContent();
				Settings.getInstance().setDragging(
						isTemplate ? new BlockNode(block, false) : BlockNode.this);
				content.put(BLOCKDATA, new HandlePosition(event.getX(), event.getY()));
				
				db.setContent(content);
				
				event.consume();
			}
		});
		
		setOnDragDone(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				event.consume();
			}
		});
		
		if (!isTemplate) {
			setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					if (event.getClickCount() > 1) {
						Option option = new Option(block);
						Editor editor = option.getBestEditor();
						editor.setModel(option);
						new EditorWindow(editor).showAndWait();
						label.setText((String)block.getOption("name"));
					}
				}
			});
		}
	}
	
	public Block getBlock() {
		if (isTemplate)
			return null;
		else
			return block;
	}
	
	public void setPosition(HandlePosition handle, double left, double top) {
		setLayoutX(left-handle.x);
		setLayoutY(top-handle.y);
	}
	
}
