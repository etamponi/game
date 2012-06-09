package game.plugins.editors.graph;


import game.core.Block;
import game.main.Settings;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
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

public class BlockNode extends VBox {
	
	public static final DataFormat BLOCKDATA = new DataFormat("game/block");
	
	private Block block;
	private boolean isTemplate;

	public BlockNode(Block b, boolean isTpl) {
		this.block = b;
		this.isTemplate = isTpl;
		
		setPadding(new Insets(5));
		
		Rectangle rect = new Rectangle(50, 50);
		rect.setFill(Color.RED);
		
		Label label = new Label(isTemplate ? block.getClass().getSimpleName() : (String)block.getOption("name"));
		label.setWrapText(true);
		
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
	}
	
	public void setPosition(HandlePosition handle, double left, double top) {
		setLayoutX(left-handle.x);
		setLayoutY(top-handle.y);
	}
	
}
