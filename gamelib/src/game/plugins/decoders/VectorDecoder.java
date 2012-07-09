package game.plugins.decoders;

import game.core.Decoder;
import game.core.Encoding;
import game.core.blocks.Encoder;
import game.plugins.encoders.VectorEncoder;

public class VectorDecoder extends Decoder<VectorEncoder> {

	@Override
	public boolean isCompatible(Encoder object) {
		return object instanceof VectorEncoder;
	}

	@Override
	public Object decode(Encoding outputEncoded) {
		return outputEncoded.get(0);
	}

}
