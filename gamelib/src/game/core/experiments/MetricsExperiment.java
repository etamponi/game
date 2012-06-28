package game.core.experiments;

import game.core.Experiment;
import game.core.blocks.Encoder;
import game.plugins.constraints.CompatibleWith;

public abstract class MetricsExperiment extends Experiment {
	
	public Encoder inputEncoder;
	
	public MetricsExperiment() {
		setOptionBinding("template.inputTemplate", "inputEncoder.template");
		
		setOptionConstraint("inputEncoder", new CompatibleWith(template, "inputTemplate"));
	}

}
