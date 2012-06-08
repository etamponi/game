package game.core.blocks;

import game.configuration.errorchecks.SizeCheck;
import game.core.InstanceTemplate;
import game.core.Block;
import game.plugins.constraints.CompatibleEncoderConstraint;

public abstract class Classifier extends Block {
	
	public InstanceTemplate template;
	
	public Encoder outputEncoder;
	
	public Classifier() {
		addOptionBinding("template.outputTemplate", "outputEncoder.template");
		addOptionChecks("parents", new SizeCheck(1));
		
		setOptionConstraint("outputEncoder", new CompatibleEncoderConstraint(this, "template.outputTemplate"));
	}

}
