package game.core;

import java.util.ArrayList;
import java.util.List;

public class Data extends ArrayList<Element> {
	
	public Data() {
		
	}
	
	public Data(List<? extends Element> other) {
		super(other);
	}
	
	public Data(Element... elements) {
		for (Element e: elements)
			add(e);
	}
	
	public int length() {
		return size();
	}
	
}
