package game.core.datatemplates;

import game.configuration.errorchecks.NoRepetitionCheck;
import game.configuration.errorchecks.SizeCheck;
import game.core.AtomicTemplate;

import java.util.LinkedList;

public class LabelTemplate extends AtomicTemplate {

	public LinkedList<String> labels;
	
	public LabelTemplate() {
		addOptionChecks("labels", new NoRepetitionCheck(), new SizeCheck(2));
	}
	
}
