package game.core.blocks;

import game.core.Block;
import game.core.Data;

import java.util.ArrayList;
import java.util.List;

import com.ios.errorchecks.SizeCheck;

public abstract class Sink extends Block {
	
	public Sink() {
		addErrorCheck("parents", new SizeCheck(1));
	}
	
	protected abstract Data combine(List<Data> inputs);

	@Override
	public Data transform(Data input) {
		List<Data> inputs = new ArrayList<>(parents.size());
		for(Block parent: parents)
			inputs.add(parent.transform(input));
		return combine(inputs);
	}

	@Override
	public boolean acceptsParents() {
		return true;
	}

}
