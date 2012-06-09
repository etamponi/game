package game.plugins.constraints;

import game.configuration.Configurable;
import game.core.InstanceTemplate;
import game.core.blocks.Classifier;
import game.plugins.Constraint;

public class CompatibleClassifierConstraint implements Constraint<Classifier> {
	
	private Configurable owner;
	private String templateOption;
	
	public CompatibleClassifierConstraint(Configurable owner, String templateOption) {
		this.owner = owner;
		this.templateOption = templateOption;
	}

	@Override
	public boolean isValid(Classifier o) {
		if (owner.getOption(templateOption) != null)
			return o.supportsTemplate((InstanceTemplate)owner.getOption(templateOption));
		else
			return false;
	}

}
