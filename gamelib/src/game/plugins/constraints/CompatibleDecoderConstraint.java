package game.plugins.constraints;

import game.configuration.Configurable;
import game.core.Decoder;
import game.plugins.Constraint;

public class CompatibleDecoderConstraint implements Constraint<Decoder> {

	private Configurable owner;
	private String encoderOption;
	
	public CompatibleDecoderConstraint(Configurable owner, String encoderOption) {
		this.owner = owner;
		this.encoderOption = encoderOption;
	}

	@Override
	public boolean isValid(Decoder o) {
		if (owner.getOption(encoderOption) != null)
			return o.getBaseEncoderClass().isAssignableFrom(owner.getOption(encoderOption).getClass());
		else
			return false;
	}

}
