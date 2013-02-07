package game.core;

import java.util.ArrayList;

public class Element extends ArrayList {
	
	public Element() {
		
	}
	
	public Element(Object... values) {
		for (Object value: values)
			add(value);
	}
	
	public <T> T get() {
		return (T)super.get(0);
	}
	
	public <T> T get(Class<T> type) {
		return (T)super.get(0);
	}
	
	public <T> T get(int index, Class<T> type) {
		return (T)super.get(index);
	}
	
}
