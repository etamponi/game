package game.plugins.pipes;

import game.core.Encoding;
import game.core.blocks.Pipe;

import java.util.List;

public class Concatenator extends Pipe {

	@Override
	protected Encoding transform(Object inputData) {
		List<Encoding> encs = getParentsEncodings(inputData);
		
		Encoding ret = new Encoding();
		
		int newElementSize = countElements(encs);
		int len = encs.get(0).length();
		for (int i = 0; i < len; i++) {
			int startIndex = 0;
			double[] element = new double[newElementSize];
			for (Encoding enc: encs) {
				System.arraycopy(enc.get(i), 0, element, startIndex, enc.getElementSize());
				startIndex += enc.getElementSize();
			}
		}
		
		return ret;
	}
	
	private int countElements(List<Encoding> encs) {
		int ret = 0;
		for (Encoding enc: encs)
			ret += enc.getElementSize();
		return ret;
	}

}
