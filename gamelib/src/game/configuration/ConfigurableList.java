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
package game.configuration;

import game.plugins.Constraint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ConfigurableList extends Configurable implements List {
	
	public Class elementType;
	
	private ArrayList internal = new ArrayList();
	
	private Object setter = null;
	
	public ConfigurableList() {
		// DO NOT USE NEVER NEVER NEVER
		elementType = Object.class;
		
		setFixedOptions("elementType");
	}
	
	public ConfigurableList(Configurable owner, Class elementType) {
		this();
		this.elementType = elementType;
		addObserver(owner);
	}
	
	public <T> List<T> getList(Class<T> type) {
		return (List<T>)internal;
	}

	@Override
	public void setOption(String optionPath, Object content, boolean notify, Object setter) {
		if (optionPath.startsWith("*.")) {
			String remainingPath = optionPath.substring(2);
			for (Object element: internal) {
				if (element != null)
					((Configurable)element).setOption(remainingPath, content, notify, setter);
			}
		} else {
			super.setOption(optionPath, content, notify, setter);
		}
	}

	@Override
	public List<String> getAllOptionNames() {
		List<String> ret = super.getAllOptionNames();
		for(int i = 0; i < internal.size(); i++)
			ret.add(String.valueOf(i));
		return ret;
	}

	@Override
	public Class getOptionType(String optionName) {
		if (optionName.equals("*") || optionName.matches("\\d+"))
			return elementType;
		else
			return super.getOptionType(optionName);
	}

	@Override
	public String getOptionNameFromContent(Object content) {
		int index = internal.indexOf(content);
		if (index < 0)
			return super.getOptionNameFromContent(content);
		else
			return String.valueOf(index);
	}

	@Override
	public boolean add(Object e) {
		add(internal.size(), e);
		return true;
	}
	
	@Override
	public void add(int index, Object element) {
		String indexString = String.valueOf(index);

		internal.add(index, element);
		
		if (element instanceof Configurable) {
			((Configurable)element).addObserver(this);
		}
		
		propagateUpdate(indexString, setter);
	}

	@Override
	public boolean addAll(Collection c) {
		return internal.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection c) {
		return internal.addAll(index, c);
	}

	@Override
	public void clear() {

		for (Object element: internal) {
			if (element instanceof Configurable)
				((Configurable)element).deleteObserver(this);
		}

		internal.clear();
	}

	@Override
	public boolean contains(Object o) {
		return internal.contains(o);
	}

	@Override
	public boolean containsAll(Collection c) {
		return internal.containsAll(c);
	}

	@Override
	public Object get(int index) {
		return internal.get(index);
	}

	public <T> T get(int index, Class<T> cast) {
		return (T)internal.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return internal.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return internal.isEmpty();
	}

	@Override
	public Iterator<Object> iterator() {
		return internal.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		return internal.lastIndexOf(o);
	}

	@Override
	public ListIterator<Object> listIterator() {
		return internal.listIterator();
	}

	@Override
	public ListIterator<Object> listIterator(int index) {
		return internal.listIterator(index);
	}

	@Override
	public boolean remove(Object o) {
		if (contains(o) && o instanceof Configurable)
			((Configurable)o).deleteObserver(this);
		
		boolean ret = internal.remove(o); 
		
		propagateUpdate("", setter);
		
		return ret;
	}

	@Override
	public Object remove(int index) {
		if (index >= 0 && index < size() && get(index) instanceof Configurable)
			((Configurable)get(index)).deleteObserver(this);
		
		Object ret = internal.remove(index);
		
		propagateUpdate("", setter);
		
		return ret;
	}

	@Override
	public boolean removeAll(Collection c) {
		return internal.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection c) {
		return internal.retainAll(c);
	}

	@Override
	public Object set(int index, Object element) {
		if (index >= 0 && index < size()) {
			if (get(index) instanceof Configurable)
				((Configurable)get(index)).deleteObserver(this);
		}
		
		Object ret = internal.set(index, element);
		
		if (index >= 0 && index < size()) {
			if (element instanceof Configurable)
				((Configurable)element).addObserver(this);
		}
		
		String indexString = String.valueOf(index);

		propagateUpdate(indexString, setter);
		
		return ret;
	}

	@Override
	public int size() {
		return internal.size();
	}

	@Override
	public List<Object> subList(int fromIndex, int toIndex) {
		return internal.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return internal.toArray();
	}

	@Override
	public Object[] toArray(Object[] a) {
		return internal.toArray(a);
	}

	@Override
	protected Object getLocalOption(String optionName) {
		if (optionName.matches("\\*"))
			return null;
		if (optionName.matches("\\d+")) {
			int index = Integer.parseInt(optionName);
			if (index < size())
				return get(index);
			else
				return null;
		} else
			return super.getLocalOption(optionName);
	}

	@Override
	protected void setLocalOption(String optionName, Object content, Object setter) {
		this.setter = setter;
		
		if (optionName.matches("\\d+")) {
			int index = Integer.parseInt(optionName);
			if (index < size())
				set(index, (Object)content);
		} else {
			switch (optionName) {
			case "add":
				add((Object)content);
				break;
			case "remove":
				if (content instanceof Integer)
					remove((int)content);
				else
					remove(content);
				break;
			default:
				super.setLocalOption(optionName, content, setter);
			}
		}
		
		this.setter = null;
	}

	@Override
	protected List<Constraint> getOptionConstraints(String optionName) {
		List<Constraint> ret = super.getOptionConstraints(optionName);
		if (optionName.matches("\\d+"))
			ret.addAll(super.getOptionConstraints("*"));
		return ret;
	}

}
