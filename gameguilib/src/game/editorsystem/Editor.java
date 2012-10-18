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
package game.editorsystem;

import game.configuration.Change;
import game.utils.Utils;

import java.util.Observable;
import java.util.Observer;

import javafx.scene.Node;

public abstract class Editor implements Observer {
	
	private Option model;
	private boolean readOnly;
	
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
		
		if (this.model != null)
			disconnect();
		
		this.model = model;
		this.model.getOwner().addObserver(this);
		
		updateView();
	}

	public void disconnect() {
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
	
	public void setReadOnly(boolean readOnly) {
		if (this.readOnly != readOnly) {
			this.readOnly = readOnly;
			if (getModel() != null)
				updateView();
		}
	}
	
	public boolean isReadOnly() {
		return readOnly;
	}

	@Override
	public void update(Observable observed, Object m) {
		if (m instanceof Change) {
			Change change = (Change)m;
			if (model != null && change.getPath().equals(model.getOptionName())) {
				if (change.getSetter() != this)
					updateView();
			}
		}
	}

}
