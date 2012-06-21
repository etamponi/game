package game.core;

import game.configuration.Configurable;
import game.configuration.errorchecks.CompatibilityCheck;
import game.plugins.constraints.Compatible;

import java.util.Map;

public abstract class Evaluator extends Configurable implements Compatible<InstanceTemplate> {
	
	public InstanceTemplate template;
	
	public Evaluator() {
		addOptionChecks("template", new CompatibilityCheck(this));
	}
	
	public abstract Map<String, Double> evaluate(Dataset dataset, String logPrefix);

}
