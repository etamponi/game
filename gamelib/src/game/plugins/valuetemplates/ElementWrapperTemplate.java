package game.plugins.valuetemplates;

import game.core.ElementTemplate;
import game.core.ValueTemplate;

import java.util.List;

public class ElementWrapperTemplate extends ValueTemplate {
	
	public ElementTemplate internalTemplate;
	
	public ElementWrapperTemplate() {
		internalTemplate = new ElementTemplate();
	}
	
	public ElementWrapperTemplate(ElementTemplate internal) {
		setContent("internalTemplate", internal);
	}

	@Override
	public int getDescriptionLength() {
		return internalTemplate.getDescriptionLength();
	}

	@Override
	public Object loadValue(List description) {
		return internalTemplate.loadElement(description);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof ElementWrapperTemplate) {
			return internalTemplate.equals(((ElementWrapperTemplate) other).internalTemplate);
		} else {
			return false;
		}
	}

}
