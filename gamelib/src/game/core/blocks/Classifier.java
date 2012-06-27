package game.core.blocks;

import game.configuration.errorchecks.SizeCheck;
import game.core.Encoding;


public abstract class Classifier extends Transducer {
	
	public Classifier() {
		setOptionChecks("parents", new SizeCheck(1, 1));
	}
	
	protected abstract Encoding classify(Encoding inputEncoded);

	@Override
	protected Encoding transform(Object inputData) {
		return classify(getParentEncoding(0, inputData));
	}

}
