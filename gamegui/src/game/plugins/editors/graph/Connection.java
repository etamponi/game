package game.plugins.editors.graph;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;

public class Connection extends CubicCurve {
	
	private AnchorPane root;
	private HBox from;
	private HBox to;

	public Connection(final AnchorPane graphRoot, final HBox from, final HBox to) {
		this.root = graphRoot;
		this.from = from;
		this.to = to;
		
		this.setStroke(Color.BLACK);
		this.setFill(Color.TRANSPARENT);
		
		ChangeListener<Number> listener = new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				setStartX(from.getLayoutX()+from.getWidth()-20);
				setStartY(from.getLayoutY()+from.getHeight()/2);
				setEndX(to.getLayoutX());
				setEndY(to.getLayoutY()+to.getHeight()/2);
				setControlX1(getEndX());
				setControlY1(getStartY());
				setControlX2(getStartX());
				setControlY2(getEndY());
			}
		};
		listener.changed(null, null, null);

		from.layoutXProperty().addListener(listener);
		from.layoutYProperty().addListener(listener);
		from.heightProperty().addListener(listener);
		to.layoutXProperty().addListener(listener);
		to.layoutYProperty().addListener(listener);
		to.heightProperty().addListener(listener);
	}
	
	public boolean matches(HBox from, HBox to) {
		return this.from == from && this.to == to;
	}
	
	public boolean relativeTo(HBox box) {
		return this.from == box || this.to == box;
	}
	
	public boolean invalid() {
		int count = 0;
		for (Node child: root.getChildren()) {
			if (child == from || child == to)
				count++;
		}
		if (count == 2)
			return false;
		else
			return true;
	}
	
}
