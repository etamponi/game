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

import game.Settings;
import game.configuration.Configurable;
import game.editorsystem.constraints.CanEditConstraint;
import game.plugins.Implementation;
import game.plugins.PluginManager;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

public class Option {
	
	public static class Temporary extends Configurable {
		
		public Object content;
		
		public Temporary() { }
		
		public Temporary(Object content) {
			this.setOption("content", content);
		}
	}
	
	private Configurable owner;
	private String optionName;
	
	public Option(Object content) {
		this.owner = new Temporary(content);
		this.optionName = "content";
	}
	
	public Option(Configurable owner, String optionName) {
		this.owner = owner;
		this.optionName = optionName;
	}
	
	public Configurable getOwner() {
		return owner;
	}
	
	public String getOptionName() {
		return optionName;
	}
	
	public <T> T getContent() {
		return owner.getOption(optionName);
	}
	
	public void setContent(Object content) {
		owner.setOption(optionName, content);
	}
	
	public void setContent(Object content, Object setter) {
		owner.setOption(optionName, content, true, setter);
	}
	
	public Class getType() {
		return getType(false);
	}
	
	public Class getType(boolean runtimeClass) {
		if (runtimeClass && getContent() != null)
			return getContent().getClass();
		else
			return owner.getOptionType(optionName);
	}
	
	public <T> SortedSet<Implementation<T>> getCompatibleImplementations() {
		return owner.getCompatibleOptionImplementations(optionName, Settings.getPluginManager());
	}
	
	public boolean isBound() {
		if (optionName.equals("this"))
			return false;
		else
			return !owner.getUnboundOptionNames().contains(optionName);
	}
	
	public Editor getBestEditor(boolean runtimeClass) {
		Class type = getType(runtimeClass);
		Set<Implementation<Editor>> editors = PluginManager.getCompatibleImplementationsOf(Editor.class, new CanEditConstraint(type));
		Iterator<Implementation<Editor>> it = editors.iterator();
		
		if (!it.hasNext())
			return null;
		
		Editor best = it.next().getContent();
		int bestDistance = distance(best.getBaseEditableClass(), type);
		while (it.hasNext()) {
			Editor current = it.next().getContent();
			int currDistance = distance(current.getBaseEditableClass(), type);
			if (currDistance < bestDistance) {
				best = current;
				bestDistance = currDistance;
			}
		}

		return best;
	}
	
	@Override
	public String toString() {
		if (getContent() == null)
			return "<null>";
		else
			return getContent().toString();
	}

	private int distance(Class origin, Class target) {
		Set<Class> currents = new HashSet<>();
		currents.add(target);
		int d = 0;
		while(!currents.contains(origin) && !currents.isEmpty()) {
			currents = findAllParents(currents);
			d++;
		}
		return d;
	}
	
	private Set<Class> findAllParents(Set<Class> currents) {
		Set<Class> parents = new HashSet<>();
		for (Class target: currents) {
			if (target.getSuperclass() != null)
				parents.add(target.getSuperclass());
			for (Class i: target.getInterfaces())
				parents.add(i);
		}
		return parents;
	}

	public <T> T cloneContent() {
		Configurable ownerClone = owner.cloneConfiguration();
		T ret = ownerClone.getOption(optionName);
		ownerClone.setOption(optionName, null);
		return ret;
	}

}
