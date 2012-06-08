package game.core.blocks;

import game.configuration.errorchecks.SizeCheck;
import game.core.DataTemplate;
import game.core.Dataset;
import game.core.Block;

public abstract class Encoder<DT extends DataTemplate> extends Block {
	
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
