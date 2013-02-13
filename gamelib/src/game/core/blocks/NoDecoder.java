package game.core.blocks;

import game.core.Data;
import game.core.DatasetTemplate;

public class NoDecoder extends Decoder {

	@Override
	public boolean isCompatible(DatasetTemplate object) {
		return true;
	}

	@Override
	public Data transform(Data input) {
		return null;
	}

}
