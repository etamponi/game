package game.plugins.editors;

import game.core.Block;

public class BlockEditor extends IObjectEditor {
	
	public BlockEditor() {
		this.getUpdateTrigger().getSubPaths().add("trainingAlgorithm");
	}

	@Override
	public Class getBaseEditableClass() {
		return Block.class;
	}

}
