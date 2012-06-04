package game.core.nodes;

import game.configuration.errorchecks.SizeCheck;
import game.core.InstanceTemplate;
import game.core.Node;

public abstract class Classifier extends Node {
	
	public InstanceTemplate template;
	
	public Encoder outputEncoder;
	
	public Classifier() {
		addOptionChecks("parents", new SizeCheck(1));
		addOptionBinding("template.outputTemplate", "outputEncoder.template");
	}

}
