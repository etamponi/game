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
package game.editorsystem;

import game.configuration.Configurable.Change;
import game.utils.Utils;

import java.util.Observable;
import java.util.Observer;

import javafx.scene.Node;

public abstract class Editor implements Observer {
	
	private Option model;
	
	public abstract Node getView();
	
	public abstract void connectView();
	
	public abstract void updateView(Change change);
	
	public abstract Class getBaseEditableClass();
	
	public Option getModel() {
		return model;
	}
	
	public void setModel(Option model) {
		if (this.model != null)
			this.model.getOwner().deleteObserver(this);
		this.model = model;
		if (this.model != null) {
			this.model.getOwner().addObserver(this);
		}
		connectView();
	}
	
	public boolean canEdit(Class type) {
		return Utils.isImplementation(type, getBaseEditableClass());
	}

	@Override
	public void update(Observable observed, Object m) {
		if (m instanceof Change) {
			Change change = (Change)m;
			if (change.pathContains(model.getOptionName())) {
				if (!change.getPath().contains("."))
					connectView();
				else
					updateView(change);
			}
		}
	}

}
