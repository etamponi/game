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

import com.ios.IObject;
import com.ios.Listener;
import com.ios.PluginManager;
import com.ios.Property;
import com.ios.Trigger;
import com.ios.triggers.SimpleTrigger;

public abstract class PropertyEditor {
	
	private static class PropertyRoot extends IObject {
		
		@SuppressWarnings("unused")
		public IObject root;
		
		public PropertyRoot(Trigger trigger) {
			addTrigger(trigger);
		}
		
	}
	
	protected class UpdateTrigger extends SimpleTrigger {
		
		private List<String> subPaths = new ArrayList<>();
		
		private class DynamicPathListener extends Listener {
			@Override
			public boolean isListeningOn(Property path) {
				if (listened == null)
					return false;
				boolean ret = listened.includes(path);
				if (!ret) {
					for(String subPath: subPaths) {
						Property temp = new Property(listened.getRoot(), listened.getPath() + "." + subPath);
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
			if (listening)
				updateView();
		}
		
	}
	
	private Property listened;
	
	private UpdateTrigger trigger = new UpdateTrigger();
	
	private PropertyRoot pRoot = new PropertyRoot(trigger);
	
	private boolean listening = true;
	
	private boolean readOnly = false;
	
	public abstract Node getView();
	
	public abstract void updateView();
	
	public abstract boolean isInline();
	
	public abstract Class getBaseEditableClass();
	
	public void connect(Property model) {
		this.listened = new Property(pRoot, "root." + model.getPath());
		
		pRoot.setContent("root", model.getRoot());
		
		updateView();
	}
	
	public Property getParent() {
		return new Property(pRoot, "root");
	}
	
	public Property getModel() {
		return listened != null ? listened.getLocalProperty() : null;
	}
	
	public void detach() {
		pRoot.detach();
	}
	
	protected UpdateTrigger getTrigger() {
		return trigger;
	}

	public boolean canEdit(Class type) {
		return Utils.isImplementation(type, getBaseEditableClass());
	}
	
	protected void updateModel(Object content) {
		listening = false;
		listened.setContent(content);
		listening = true;
	}
	
	public void setReadOnly(boolean readOnly) {
		if (this.readOnly != readOnly) {
			this.readOnly = readOnly;
			if (pRoot != null)
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

}
