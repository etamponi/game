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
package game.plugins.editors;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import game.editorsystem.Editor;

public class BooleanEditor extends Editor {
	
	private CheckBox box = new CheckBox();
	
	public BooleanEditor() {
		box.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				if (getModel() != null) {
					getModel().setContent(newValue);
				}
			}
		});
	}

	@Override
	public Node getView() {
		return box;
	}

	@Override
	public void connectView() {
		
	}

	@Override
	public void updateView() {
		if (getModel() != null)
			box.setSelected((boolean)getModel().getContent());
		else
			box.setSelected(false);
	}

	@Override
	public void updateModel() {
		// See the constructor
	}

	@Override
	public Class getBaseEditableClass() {
		return Boolean.class;
	}

	@Override
	public boolean canEdit(Class type) {
		return super.canEdit(type) || type == boolean.class;
	}

}
