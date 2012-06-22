package game.core;

import game.configuration.errorchecks.CompatibilityCheck;
import game.plugins.constraints.Compatible;

import java.util.Map;

public abstract class Evaluator extends LongTask implements Compatible<InstanceTemplate> {
	
	public static final String TASKNAME = "evaluate";
	
	public InstanceTemplate template;
	
	public Evaluator() {
		addOptionChecks("template", new CompatibilityCheck(this));
	}
	
	public Map<String, Double> startEvaluation(Dataset dataset) {
		return startTask(TASKNAME, dataset);
	}
	
	protected abstract Map<String, Double> evaluate(Dataset dataset);

	@Override
	protected Object execute(Object... params) {
		return evaluate((Dataset)params[0]);
	}

}
