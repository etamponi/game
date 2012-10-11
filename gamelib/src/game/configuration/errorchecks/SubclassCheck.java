package game.configuration.errorchecks;

import game.configuration.ErrorCheck;

public class SubclassCheck implements ErrorCheck {
	
	private Class base;

	public SubclassCheck(Class base) {
		this.base = base;
	}

	@Override
	public String getError(Object value) {
		if (!base.isAssignableFrom(value.getClass()))
			return "must be a subclass of " + base.getSimpleName();
		else
			return null;
	}

}
