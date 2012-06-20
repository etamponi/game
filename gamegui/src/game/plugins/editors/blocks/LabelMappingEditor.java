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
package game.plugins.editors.blocks;

import game.configuration.Configurable.Change;
import game.editorsystem.Editor;
import game.plugins.encoders.LabelEncoder;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

public class LabelMappingEditor extends Editor {
	
	GridPane root = new GridPane();

	@Override
	public Node getView() {
		return root;
	}

	@Override
	public boolean isInline() {
		return false;
	}

	@Override
	public void connectView() {
		if (getModel() == null || getModel().getContent() == null) {
			root.getChildren().clear();
			return;
		}
		
		int row = 0;
		final LabelEncoder encoder = (LabelEncoder)getModel().getOwner();
		for (final String label: encoder.template.labels.getList(String.class)) {
			Label l = new Label(label);
			
			if (label == null)
				continue;
			
			final TextField tf = new TextField(toText(encoder.labelMapping.get(label)));
			tf.setOnKeyReleased(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					double[] enc = fromText(tf.getText());
					if (enc != null)
						encoder.labelMapping.put(label, enc);
				}
			});
			
			root.addRow(row++, l, tf);
		}
	}
	
	public String toText(double[] v) {
		if (v == null)
			return "";
		
		StringBuilder ret = new StringBuilder();
		
		int count = 0;
		for (double e: v)
			ret.append(String.format("%.0f", e)).append(++count < v.length ? ", " : "");
		
		return ret.toString();
	}
	
	public double[] fromText(String text) {
		String[] tokens = text.split(" *, *");
		double[] ret = new double[tokens.length];
		int i = 0;
		for (String token: tokens) {
			try {
				ret[i] = Double.parseDouble(token);
			} catch (NumberFormatException ex) {
				return null;
			}
			i++;
		}
		return ret;
	}

	@Override
	public void updateView(Change change) {
		connectView();
	}

	@Override
	public Class getBaseEditableClass() {
		return getClass();
	}

}
