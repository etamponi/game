package game.plugins.datatemplates;

import game.configuration.ConfigurableList;
import game.configuration.errorchecks.NoRepetitionCheck;
import game.configuration.errorchecks.SizeCheck;

public class LabelTemplate extends AtomicTemplate {

	public ConfigurableList labels = new ConfigurableList(this, String.class);
	
	public LabelTemplate() {
		addOptionChecks("labels", new NoRepetitionCheck(), new SizeCheck(2));
	}
	
}
