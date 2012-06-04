package game.core.datatemplates;

import game.configuration.errorchecks.PositivenessCheck;

public class VectorTemplate extends AtomicTemplate {

	public int featureNumber;
	
	public VectorTemplate() {
		addOptionChecks("featureNumber", new PositivenessCheck(false));
	}
	
}
