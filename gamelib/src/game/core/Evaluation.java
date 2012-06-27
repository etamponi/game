package game.core;

import game.configuration.Configurable;
import game.configuration.errorchecks.CompatibilityCheck;
import game.plugins.constraints.Compatible;

public abstract class Evaluation extends Configurable implements Compatible<InstanceTemplate> {
	
	public InstanceTemplate template;
	
	public Evaluation() {
		name = getClass().getSimpleName();
		setOptionChecks("template", new CompatibilityCheck(this));
	}
	
	public abstract boolean isReady();
	
	public abstract void evaluate(Dataset dataset);
	
	public abstract String prettyPrint();
	
}
