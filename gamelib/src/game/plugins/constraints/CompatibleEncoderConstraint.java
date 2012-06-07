package game.plugins.constraints;

import game.configuration.Configurable;
import game.core.nodes.Encoder;
import game.plugins.Constraint;

public class CompatibleEncoderConstraint implements Constraint<Encoder> {
	
	private Configurable owner;
	private String templateOption;
	
	public CompatibleEncoderConstraint(Configurable owner, String templateOption) {
		this.owner = owner;
		this.templateOption = templateOption;
	}

	@Override
	public boolean isValid(Encoder o) {
		if (owner.getOption(templateOption) != null)
			return o.getBaseTemplateClass().isAssignableFrom(owner.getOption(templateOption).getClass());
		else
			return false;
	}

}
