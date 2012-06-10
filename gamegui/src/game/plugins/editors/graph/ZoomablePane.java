package game.plugins.editors.graph;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.transform.Scale;

public class ZoomablePane extends ScrollPane {

	private AnchorPane parent = new AnchorPane();
	private AnchorPane content = new AnchorPane();
	private double minimum = 0.1;
	private double level = 1;
	
	public ZoomablePane(double width, double height) {
		String image = getClass().getResource("background.png").toExternalForm();
		
		content.setStyle("-fx-background-image: url('" + image + "');" + 
		           		 "-fx-background-position: 0 0;" +
		           		 "-fx-background-repeat: repeat;");
		
		content.setPrefSize(width, height);
		content.setMinSize(width, height);
		content.setMaxSize(width, height);
		parent.getChildren().add(content);
		setPannable(true);
		setContent(parent);
		updateContentPane();
	}
	
	public void setZoomLevel(double level) {
		assert(level >= minimum && level <= 1);
		this.level = level;
		updateContentPane();
	}
	
	private void updateContentPane() {
		content.getTransforms().clear();
		content.getTransforms().add(new Scale(level, level));
		parent.setPrefSize(content.getWidth()*level, content.getHeight()*level);
		parent.setMinSize(content.getWidth()*level, content.getHeight()*level);
		parent.setMaxSize(content.getWidth()*level, content.getHeight()*level);
	}
	
	public AnchorPane getContentPane() {
		return content;
	}
	
}
