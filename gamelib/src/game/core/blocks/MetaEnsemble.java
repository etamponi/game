package game.core.blocks;

import game.core.Data;
import game.core.ElementTemplate;

public class MetaEnsemble extends Pipe {
	
	public Sink combiner;

	@Override
	protected Data transduce(Data input) {
		return combiner.transform(input);
	}

	@Override
	public boolean supportsInputTemplate(ElementTemplate inputTemplate) {
		return true; // FIXME
	}

	@Override
	protected void setup() {
		// Nothing to do
	}

}
