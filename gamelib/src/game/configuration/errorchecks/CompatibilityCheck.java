package game.configuration.errorchecks;

import game.configuration.ErrorCheck;
import game.plugins.constraints.Compatible;

public class CompatibilityCheck implements ErrorCheck {
	
	private Compatible owner;
	
	public CompatibilityCheck(Compatible owner) {
		this.owner = owner;
	}

	@Override
	public String getError(Object value) {
		if (!owner.isCompatible(value))
			return "this value is not compatible with the option";
		else
			return null;
	}

}
