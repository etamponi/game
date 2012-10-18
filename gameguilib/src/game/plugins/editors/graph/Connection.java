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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;

public class Connection extends CubicCurve {
	
	private AnchorPane root;
	private BlockNode from;
	private BlockNode to;

	public Connection(final AnchorPane graphRoot, BlockNode from, BlockNode to) {
		this.root = graphRoot;
		this.from = from;
		this.to = to;
		
		this.setStroke(Color.BLACK);
		this.setFill(Color.TRANSPARENT);
		
		final HBox fromWrapper = from.getWrapper();
		final HBox toWrapper = to.getWrapper();
		ChangeListener<Number> listener = new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				setStartX(fromWrapper.getLayoutX()+fromWrapper.getWidth()-20);
				setStartY(fromWrapper.getLayoutY()+fromWrapper.getHeight()/2);
				setEndX(toWrapper.getLayoutX());
				setEndY(toWrapper.getLayoutY()+toWrapper.getHeight()/2);

				double deltaX = Math.abs(getStartX()-getEndX());
				setControlX1(getStartX()+deltaX);
				setControlY1(getStartY());
				setControlX2(getEndX()-deltaX);
				setControlY2(getEndY());
				
				double deltaY = Math.abs(getStartY()-getEndY());
				if (deltaY < 60 && getEndX() < getStartX()) {
					setControlY1(getStartY()+5*Math.sqrt(deltaX));
					setControlY2(getEndY()+5*Math.sqrt(deltaX));
				}
			}
		};
		listener.changed(null, null, null);

		fromWrapper.layoutXProperty().addListener(listener);
		fromWrapper.layoutYProperty().addListener(listener);
		fromWrapper.heightProperty().addListener(listener);
		toWrapper.layoutXProperty().addListener(listener);
		toWrapper.layoutYProperty().addListener(listener);
		toWrapper.heightProperty().addListener(listener);
	}
	
	public boolean matches(BlockNode from, BlockNode to) {
		return this.from == from && this.to == to;
	}
	
	public boolean relativeTo(BlockNode node) {
		return this.from == node || this.to == node;
	}
	
	public boolean isInvalid() {
		int count = 0;
		for (Node child: root.getChildren()) {
			if (child == from.getWrapper() || child == to.getWrapper())
				count++;
		}
		if (count == 2)
			return false;
		else
			return true;
	}

	public void removeFromModel() {
		to.getBlock().parents.remove(from.getBlock());
	}
	
}
