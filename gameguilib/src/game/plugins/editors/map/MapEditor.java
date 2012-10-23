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
package game.plugins.editors.map;

import game.configuration.Change;
import game.configuration.ConfigurableMap;
import game.editorsystem.ControlledEditor;

import java.util.Observable;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class MapEditor extends ControlledEditor {
	
	@Override
	public Node getView() {
		Node view = super.getView();
		GridPane.setVgrow(view, Priority.SOMETIMES);
		return view;
	}

	@Override
	public Class getBaseEditableClass() {
		return ConfigurableMap.class;
	}

	@Override
	public boolean isInline() {
		return false;
	}

	@Override
	public void update(Observable observed, Object m) {
		super.update(observed, m);
		if (m instanceof Change) {
			if (getModel() == null)
				return;
			Change change = (Change)m;
			if (change.getPath().matches(getModel().getOptionName() + "\\..+")
					|| change.getPath().matches(getModel().getOptionName() + "\\..+\\.name")) {
				if (change.getSetter() != this)
					updateView();
			}
		}
	}

}
