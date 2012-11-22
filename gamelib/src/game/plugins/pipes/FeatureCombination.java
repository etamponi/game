/*******************************************************************************
 * Copyright (c) 2012 Emanuele Tamponi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Emanuele Tamponi - initial API and implementation
 ******************************************************************************/
package game.plugins.pipes;

import game.core.DataTemplate.Data;
import game.core.Encoding;
import game.core.blocks.Pipe;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.ArithmeticUtils;

import com.ios.IObject;
import com.ios.errorchecks.RangeCheck;
import com.ios.errorchecks.RangeCheck.Bound;

public class FeatureCombination extends Pipe {
	
	public static abstract class CombinationFunction extends IObject {
		
		public int operands = 2;
		
		public CombinationFunction() {
			addErrorCheck("operands", new RangeCheck(2, Bound.LOWER));
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
			for(int i = 0; i < indices.length; i++)
				indices[i] = i;
			
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

	@Override
	public FeatureType getFeatureType(int featureIndex) {
		return FeatureType.NUMERIC;
	}
	
}
