package game.plugins.pipes;

import game.configuration.errorchecks.PositivenessCheck;
import game.configuration.errorchecks.SizeCheck;
import game.core.Block;
import game.core.Encoding;
import game.core.blocks.Pipe;

public class WindowEnlarger extends Pipe {
	
	public int windowSize = 1;
	
	public WindowEnlarger() {
		addOptionChecks("parents", new SizeCheck(1, 1));
		
		addOptionChecks("windowSize", new PositivenessCheck(false));
	}

	@Override
	protected Encoding transform(Object inputData) {
		return getParents().getList(Block.class).get(0).startTransform(inputData).makeWindowedEncoding(windowSize);
	}

}
