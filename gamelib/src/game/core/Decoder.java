package game.core;

import game.configuration.Configurable;
import game.core.nodes.Encoder;

public abstract class Decoder<E extends Encoder> extends Configurable {
	
	public E encoder;
	
	public abstract Object decode(Encoding outputEncoded);
	
	public abstract Class getBaseEncoderClass();

}
