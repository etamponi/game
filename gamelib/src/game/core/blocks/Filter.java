package game.core.blocks;

import game.core.Block;

public abstract class Filter extends Block {
	
	@Override
	public boolean isClassifier() {
		return false;
	}

}
