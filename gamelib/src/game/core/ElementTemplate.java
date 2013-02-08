package game.core;

import java.util.Arrays;
import java.util.List;

import com.ios.IList;
import com.ios.errorchecks.SizeCheck;

public class ElementTemplate extends IList<ValueTemplate> {

	public ElementTemplate() {
		super(ValueTemplate.class);
		
		addErrorCheck("", new SizeCheck(1));
	}
	
	public ElementTemplate(ValueTemplate... templates) {
		this();
		for(ValueTemplate template: templates)
			add(template);
	}
	
	public boolean isSingletonTemplate(Class<? extends ValueTemplate> type) {
		return size() == 1 && type.isAssignableFrom(get(0).getClass());
	}
	
	public <T extends ValueTemplate> T getSingleton() {
		return size() != 1 ? null : (T)get(0);
	}
	
	public <T extends ValueTemplate> T getSingleton(Class<T> type) {
		return size() != 1 ? null : (T)get(0);
	}
	
	public Element loadElement(String[] descriptions) {
		return loadElement(Arrays.asList(descriptions));
	}
	
	public Element loadElement(List<String> descriptions) {
		Element ret = new Element();
		int startingIndex = 0;
		for(ValueTemplate template: this) {
			List<String> description = descriptions.subList(startingIndex, startingIndex+template.getDescriptionLength());
			ret.add(template.loadValue(description));
			startingIndex += template.getDescriptionLength();
		}
		return ret;
	}

	public int getDescriptionLength() {
		int ret = 0;
		for(ValueTemplate template: this)
			ret += template.getDescriptionLength();
		return ret;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ElementTemplate) {
			ElementTemplate other = (ElementTemplate) o;
			if (other.size() != this.size())
				return false;
			for(int i = 0; i < this.size(); i++) {
				if (!other.get(i).equals(this.get(i)))
					return false;
			}
			return true;
		} else {
			return false;
		}
	}
	
}
