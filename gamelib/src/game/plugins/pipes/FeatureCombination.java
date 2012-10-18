package game.plugins.pipes;

import game.configuration.Configurable;
import game.configuration.errorchecks.RangeCheck;
import game.configuration.errorchecks.RangeCheck.RangeType;
import game.core.DataTemplate.Data;
import game.core.Encoding;
import game.core.blocks.Pipe;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.ArithmeticUtils;

public class FeatureCombination extends Pipe {
	
	public static abstract class CombinationFunction extends Configurable {
		
		public int operands = 2;
		
		public CombinationFunction() {
			setOptionChecks("operands", new RangeCheck(RangeType.LOWER, 2));
		}
		
		public abstract double evaluate(RealVector values);
		
	}
	
	public CombinationFunction function;

	@Override
	public Encoding transform(Data input) {
		Encoding ret = new Encoding(getFeatureNumber(), input.length());
		Encoding inputEnc = getParentEncoding(0, input);
		
		for (int j = 0; j < inputEnc.length(); j++) {
			int[] indices = new int[function.operands];
			indices[0] = 0;	indices[1] = 1; indices[2] = 2;
			
			for (int i = 0; i < getFeatureNumber(); i++) {
				RealVector values = new ArrayRealVector(function.operands);
				for(int k = 0; k < function.operands; k++)
					values.setEntry(k, inputEnc.getEntry(indices[k], j));
				ret.setEntry(i, j, function.evaluate(values));
				
				nextPermutation(indices, getParent(0).getFeatureNumber());
			}
		}
		
		return ret;
	}

	private static void nextPermutation(int[] indices, int baseFeatures) {
		int pos = indices.length-1;
		int maxIndex = baseFeatures - 1;
		while(true) {
			if (pos < 0)
				return;
			if (indices[pos] < maxIndex) {
				indices[pos]++;
				break;
			} else {
				pos--;
				maxIndex = maxIndex-1;
			}
		}
		for(pos = pos+1; pos < indices.length; pos++) {
			indices[pos] = indices[pos-1]+1;
		}
	}

	@Override
	public int getFeatureNumber() {
		if (parents.isEmpty() || function == null)
			return 0;
		else
			return (int)ArithmeticUtils.binomialCoefficient(getParent(0).getFeatureNumber(), function.operands);
	}
	
}
