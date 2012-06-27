package game.core.blocks;

import game.configuration.errorchecks.SizeCheck;
import game.core.Block;
import game.core.Dataset;

public abstract class Pipe extends Block {
	
	public Pipe() {
		setOptionChecks("parents", new SizeCheck(1));
	}
	
	@Override
	public boolean isTrained() {
		return true;
	}

	@Override
	protected void train(Dataset trainingSet) {
		throw new UnsupportedOperationException("You cannot train a Pipe!");
	}

	@Override
	public boolean acceptsNewParents() {
		return true;
	}

}
