package game.core.blocks;

import game.core.Block;
import game.core.Data;
import game.core.ElementTemplate;

import com.ios.errorchecks.SizeCheck;

public abstract class Pipe extends Block {
	
	public Pipe() {
		addErrorCheck("parents", new SizeCheck(1, 1));
	}
	
	protected abstract Data transduce(Data input);
	
	public Block getParent() {
		return parents.isEmpty() ? null : parents.get(0);
	}
	
	public ElementTemplate getParentTemplate() {
		return getParent() == null ? null : getParent().outputTemplate;
	}

	@Override
	public Data transform(Data input) {
		return transduce(getParent().transform(input));
	}

	@Override
	public boolean acceptsParents() {
		return true;
	}

}
