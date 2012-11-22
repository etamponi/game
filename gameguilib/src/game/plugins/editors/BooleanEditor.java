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

import game.editorsystem.PropertyEditor;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;

public class BooleanEditor extends PropertyEditor {
	
	private CheckBox box = new CheckBox();
	
	public BooleanEditor() {
		box.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				if (getModel() != null) {
					updateModel(newValue);
				}
			}
		});
	}

	@Override
	public boolean isInline() {
		return true;
	}

	@Override
	public Node getView() {
		return box;
	}

	@Override
	public void updateView() {
		box.setSelected((boolean)getModel().getContent());
		
		box.setDisable(isReadOnly());
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
