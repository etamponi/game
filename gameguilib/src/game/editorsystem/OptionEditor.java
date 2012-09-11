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

import game.configuration.Change;
import game.utils.Utils;

import java.util.Observable;
import java.util.Observer;

import javafx.scene.Node;

public abstract class OptionEditor implements Observer {
	
	private Option model;
	
	public abstract Node getView();
	
	public abstract void updateView();
	
	public abstract boolean isInline();
	
	public abstract Class getBaseEditableClass();
	
	@Override
	protected void finalize() throws Throwable {
		disconnect();
	}

	public Option getModel() {
		return model;
	}
	
	public void connect(Option model) {
		assert(model != null);
		
//		System.out.println("Connecting " + this.getClass().getSimpleName() + " to " + model.getOwner() + "." + model.getOptionName() + " = " + model.getContent());
		
		if (this.model != null)
			disconnect();
		
		this.model = model;
		this.model.getOwner().addObserver(this);
		
		updateView();
	}

	public void disconnect() {
//		System.out.println("Disconnecting " + this.getClass().getSimpleName() + " from " + model.getOwner() + "." + model.getOptionName() + " = " + model.getContent());
		
		if (this.model != null)
			this.model.getOwner().deleteObserver(this);
		this.model = null;
	}
	
	public boolean canEdit(Class type) {
		return Utils.isImplementation(type, getBaseEditableClass());
	}
	
	protected void setModelContent(Object content) {
		model.setContent(content, this);
	}

	@Override
	public void update(Observable observed, Object m) {
		if (m instanceof Change) {
			Change change = (Change)m;
			
			if (change.getPath().equals(model.getOptionName())) {
				if (change.getSetter() != this)
					updateView();
			}
		}
	}

}
