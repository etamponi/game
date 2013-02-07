package game.core;

import java.util.ArrayList;

public class Data extends ArrayList<Element> {
	
	public Data() {
		
	}
	
	public Data(Data other) {
		super(other);
	}
	
	public Data(Element... elements) {
		for (Element e: elements)
			add(e);
	}
	
	public int length() {
		return size();
	}
	
	public Element get() {
		return get(0);
	}
	/*
	public <T> T getValue() {
		return get().get();
	}
	
	public <T> T getValue(Class<T> type) {
		return get().get();
	}
	*/
}
