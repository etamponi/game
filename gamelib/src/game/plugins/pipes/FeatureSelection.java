package game.plugins.pipes;

import game.configuration.ErrorCheck;
import game.core.DataTemplate.Data;
import game.core.Encoding;
import game.core.blocks.Pipe;

import org.apache.commons.math3.linear.RealVector;

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
	public Encoding transform(Data input) {
		Encoding ret = new Encoding(getFeatureNumber(), input.size());
		Encoding base = getParentEncoding(0, input);
		
		for(int j = 0; j < input.size(); j++) {
			RealVector baseElement = base.getElement(j);
			int baseIndex = 0, i = 0;
			for(char c: mask.toCharArray()) {
				if (c == '1') {
					ret.setEntry(i, j, baseElement.getEntry(baseIndex));
					i++;
				}
				baseIndex++;
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
