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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigurableMap extends Configurable implements Map<String, Object> {

	public Class elementType;
	
	private Map<String, Object> internal = new HashMap<>();
	
	private Object setter = null;
	
	@Deprecated
	public ConfigurableMap() {
		// Can be used only by ConfigurationConverter
		elementType = Object.class;
		
		setAsInternalOptions("elementType");
	}
	
	public ConfigurableMap(Configurable owner, Class elementType) {
		this();
		this.elementType = elementType;
		addObserver(owner);
	}
	
	@Override
	public void clear() {
		for(Object value: internal.values()) {
			if (value instanceof Configurable)
				((Configurable) value).deleteObserver(this);
		}
		internal.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return internal.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return internal.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return internal.entrySet();
	}

	public <T> Set<Map.Entry<String, T>> entrySet(Class<T> type) {
		Set<Entry<String, T>> ret = new HashSet<>();
		for(Entry<String, Object> entry: internal.entrySet())
			ret.add((Entry<String, T>)entry);
		return ret;
	}

	@Override
	public Object get(Object key) {
		return internal.get(key);
	}
	
	public <T> T get(Object key, Class<T> type) {
		return (T)internal.get(key);
	}

	@Override
	public boolean isEmpty() {
		return internal.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		return internal.keySet();
	}

	@Override
	public Object put(String key, Object value) {
		if (key.contains("."))
			throw new IllegalArgumentException("Key cannot contain dots");
		if (value != null && !elementType.isAssignableFrom(value.getClass()))
			throw new IllegalArgumentException("Value must be a " + elementType.getClass());
		
		Object oldValue = internal.put(key, value);
		if (oldValue != null && oldValue instanceof Configurable)
			((Configurable)oldValue).deleteObserver(this);
		
		if (value instanceof Configurable)
			((Configurable) value).addObserver(this);
		
		propagateUpdate(key, setter);
		
		return oldValue;
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		for (java.util.Map.Entry<? extends String, ? extends Object> entry: m.entrySet())
			put(entry.getKey(), entry.getValue());
	}

	@Override
	public Object remove(Object key) {
		if (!internal.containsKey(key))
			return null;
		
		Object oldValue = internal.remove(key);
		if (oldValue != null && oldValue instanceof Configurable)
			((Configurable)oldValue).deleteObserver(this);
		
		propagateUpdate("", setter);
		
		return oldValue;
	}

	@Override
	public int size() {
		return internal.size();
	}

	@Override
	public Collection<Object> values() {
		return internal.values();
	}
	
	public <T> Collection<T> values(Class<T> type) {
		List<T> ret = new ArrayList<>(values().size());
		for(Object o: values())
			ret.add((T)o);
		return ret;
	}

	@Override
	public List<String> getAllOptionNames() {
		List<String> ret = super.getAllOptionNames();
		ret.addAll(internal.keySet());
		return ret;
	}

	@Override
	public Class getOptionType(String optionName) {
		Class ret = super.getOptionType(optionName);
		if (ret == null && internal.keySet().contains(optionName))
			return elementType;
		else
			return ret;
	}

	@Override
	public String getOptionNameFromContent(Object content) {
		String ret = super.getOptionNameFromContent(content);
		if (ret == null) {
			for(String key: internal.keySet())
				if (internal.get(key).equals(content))
					ret = key;
		}
		return ret;
	}

	@Override
	protected Object getLocalOption(String optionName) {
		Object ret = super.getLocalOption(optionName);
		if (ret == null && containsKey(optionName))
			ret = get(optionName);
		return ret;
	}

	@Override
	protected void setLocalOption(String optionName, Object content, Object setter) {
		if (super.getLocalOption(optionName) != null) {
			super.setLocalOption(optionName, content, setter);
		} else {
			this.setter = setter;
			
			put(optionName, content);
			
			this.setter = null;
		}
	}
	

}
