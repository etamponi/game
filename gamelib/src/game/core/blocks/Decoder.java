package game.core.blocks;

import game.core.Block;

import com.ios.triggers.MasterSlaveTrigger;

public abstract class Decoder extends Block {
	
	public Decoder() {
		addTrigger(new MasterSlaveTrigger(this, "datasetTemplate.targetTemplate", "outputTemplate"));
	}
	
	@Override
	public boolean isClassifier() {
		return false;
	}
	
	@Override
	protected void updateOutputTemplate() {
		// Nothing to do
	}

}
