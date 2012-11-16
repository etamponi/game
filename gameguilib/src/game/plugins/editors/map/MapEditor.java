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

import com.ios.IMap;

import game.editorsystem.ControlledEditor;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class MapEditor extends ControlledEditor {
	
	public MapEditor() {
		getUpdateTrigger().getSubPaths().add("*");
		getUpdateTrigger().getSubPaths().add("*.name");
	}
	
	@Override
	public Node getView() {
		Node view = super.getView();
		GridPane.setVgrow(view, Priority.SOMETIMES);
		return view;
	}

	@Override
	public Class getBaseEditableClass() {
		return IMap.class;
	}

	@Override
	public boolean isInline() {
		return false;
	}

}
