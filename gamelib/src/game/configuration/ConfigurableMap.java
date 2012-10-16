package game.configuration;

import java.util.Collection;
import java.util.HashMap;
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
		
		setPrivateOptions("elementType");
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

	@Override
	public Object get(Object key) {
		return internal.get(key);
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

	@Override
	public List<String> getAllOptionNames() {
		List<String> ret = super.getAllOptionNames();
		ret.addAll(internal.keySet());
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
