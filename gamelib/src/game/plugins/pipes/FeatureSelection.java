package game.plugins.pipes;

import java.util.List;

import game.configuration.ErrorCheck;
import game.core.Encoding;
import game.core.blocks.Pipe;

public class FeatureSelection extends Pipe {
	
	public String mask;
	
	public FeatureSelection() {
		setOptionChecks("mask", new ErrorCheck<String>() {
			@Override public String getError(String value) {
				if (count(mask, '0') + count(mask, '1') != mask.length())
					return "can contain only 1s and 0s";
				if (!parents.isEmpty() && getParent(0).getFeatureNumber() != mask.length())
					return "must contain extactly " + getParent(0).getFeatureNumber() + " characters";
				return null;
			}
		});
	}

	@Override
	public Encoding transform(List input) {
		Encoding ret = new Encoding();
		Encoding base = getParentEncoding(0, input);
		
		for(double[] baseElement: base) {
			double[] element = new double[getFeatureNumber()];
			int i = 0, j = 0;
			for(char c: mask.toCharArray()) {
				if (c == '1') {
					element[j] = baseElement[i];
					j++;
				}
				i++;
			}
		}
		
		return ret;
	}

	@Override
	public int getFeatureNumber() {
		return count(mask, '1');
	}

	private int count(String s, char c) {
		int count = 0;
		for(char curr: s.toCharArray())
			if (curr == c)
				count++;
		return count;
	}

}
