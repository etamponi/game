package game.plugins.editors;

import game.core.Block;

public class BlockEditor extends ConfigurableEditor {

	public BlockEditor() {
		addHiddenOption("parents");
	}

	@Override
	public Class getBaseEditableClass() {
		return Block.class;
	}
	
}
