package game.configuration.errorchecks;

import game.configuration.ErrorCheck;

import java.util.List;

public class ListMustContainCheck implements ErrorCheck<List> {
	
	private Object element;

	public ListMustContainCheck(Object element) {
		this.element = element;
	}

	@Override
	public String getError(List value) {
		if (!value.contains(element))
			return "list must contain " + element;
		else
			return null;
	}

}
