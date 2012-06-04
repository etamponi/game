package game.core.datatemplates;

import game.configuration.errorchecks.NoRepetitionCheck;
import game.configuration.errorchecks.SizeCheck;

import java.util.LinkedList;

public class LabelTemplate extends AtomicTemplate {

	public LinkedList<String> labels = new LinkedList<>();
	
	public LabelTemplate() {
		addOptionChecks("labels", new NoRepetitionCheck(), new SizeCheck(2));
	}
	
}
