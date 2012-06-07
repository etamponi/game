package game.core.nodes;

import game.configuration.errorchecks.SizeCheck;
import game.core.DataTemplate;
import game.core.Dataset;
import game.core.Node;

public abstract class Encoder<DT extends DataTemplate> extends Node {
	
	public DT template;
	
	public Encoder() {
		addOptionChecks("parents", new SizeCheck(0, 0));
	}

	@Override
	public boolean isTrained() {
		return true;
	}

	@Override
	protected double train(Dataset trainingSet) {
		throw new UnsupportedOperationException("You cannot train an Encoder!");
	}
	
	public abstract Class getBaseTemplateClass();

}
