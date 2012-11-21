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

import game.editorsystem.constraints.CanEditConstraint;
import game.utils.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javafx.scene.Node;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoCopyable;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.ios.IObject;
import com.ios.Listener;
import com.ios.PluginManager;
import com.ios.Property;
import com.ios.triggers.SimpleTrigger;

public abstract class PropertyEditor extends IObject implements KryoSerializable, KryoCopyable<PropertyEditor> {
	
	protected class UpdateTrigger extends SimpleTrigger {
		
		private List<String> subPaths = new ArrayList<>();
		
		private class DynamicPathListener extends Listener {
			@Override
			public boolean isListeningOn(Property path) {
				boolean ret = reference.includes(path);
				if (!ret) {
					for(String subPath: subPaths) {
						Property temp = new Property(reference.getRoot(), reference.getPath() + "." + subPath);
						if (temp.includes(path))
							return true;
					}
				}
				return ret;
			}
		}
		
		public UpdateTrigger() {
			getListeners().add(new DynamicPathListener());
		}
		
		public List<String> getSubPaths() {
			return subPaths;
		}
		
		@Override
		public void action(Property changedPath) {
			if (root != null && listening)
				updateView();
		}
		
	}
	
	public IObject root;
	
	private Property reference, model;
	
	private UpdateTrigger trigger = new UpdateTrigger();
	
	private boolean listening = true;
	
	private boolean readOnly = false;
	
	public abstract Node getView();
	
	public abstract void updateView();
	
	public abstract boolean isInline();
	
	public abstract Class getBaseEditableClass();
	
	public PropertyEditor() {
		addTrigger(trigger);
	}
	
	public void connect(Property model) {
		this.reference = new Property(this, "root." + model.getPath());
		this.model = model;
		
		setContent("root", model.getRoot());
		
		updateView();
	}
	
	public Property getModel() {
		return root != null ? model : null;
	}
	
	protected UpdateTrigger getUpdateTrigger() {
		return trigger;
	}

	public boolean canEdit(Class type) {
		return Utils.isImplementation(type, getBaseEditableClass());
	}
	
	protected void updateModel(Object content) {
		listening = false;
		model.setContent(content);
		listening = true;
	}
	
	public void setReadOnly(boolean readOnly) {
		if (this.readOnly != readOnly) {
			this.readOnly = readOnly;
			if (root != null)
				updateView();
		}
	}
	
	public boolean isReadOnly() {
		return readOnly;
	}
	
	public static PropertyEditor getBestEditor(Class type) {
		Set<Class> editors = PluginManager.getCompatibleImplementationsOf(PropertyEditor.class, new CanEditConstraint(type));
		Iterator<Class> it = editors.iterator();
		
		if (!it.hasNext())
			return null;
		
		PropertyEditor best = null;
		Class currentType = it.next();
		try {
			best = (PropertyEditor) currentType.newInstance();
			int bestDistance = distance(best.getBaseEditableClass(), type);
			while (it.hasNext()) {
				currentType = it.next();
				PropertyEditor current = (PropertyEditor) currentType.newInstance();
				int currDistance = distance(current.getBaseEditableClass(), type);
				if (currDistance < bestDistance) {
					best = current;
					bestDistance = currDistance;
				}
			}
		} catch (InstantiationException | IllegalAccessException e) {
			System.out.println("Cannot instantiate Editor of type " + currentType);
		}

		return best;
	}

	private static int distance(Class origin, Class target) {
		Set<Class> currents = new HashSet<>();
		currents.add(target);
		int d = 0;
		while(!currents.contains(origin) && !currents.isEmpty()) {
			currents = findAllParents(currents);
			d++;
		}
		return d;
	}
	
	private static Set<Class> findAllParents(Set<Class> currents) {
		Set<Class> parents = new HashSet<>();
		for (Class target: currents) {
			if (target.getSuperclass() != null)
				parents.add(target.getSuperclass());
			for (Class i: target.getInterfaces())
				parents.add(i);
		}
		return parents;
	}

	@Override
	public PropertyEditor copy(Kryo kryo) {
		throw new UnsupportedOperationException("Cannot copy a " + getClass());
	}

	@Override
	public void read(Kryo kryo, Input input) {
		throw new UnsupportedOperationException("Cannot read a " + getClass());
	}

	@Override
	public void write(Kryo kryo, Output output) {
		throw new UnsupportedOperationException("Cannot write a " + getClass());
	}

}
