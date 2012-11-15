/*******************************************************************************
 * Copyright (c) 2012 Emanuele.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Emanuele - initial API and implementation
 ******************************************************************************/
package game.plugins.pipes;

import game.core.DataTemplate.Data;
import game.core.Encoding;
import game.core.blocks.Pipe;
import game.utils.Utils;

import org.apache.commons.math3.linear.RealVector;

import com.ios.ErrorCheck;

public class FeatureSelection extends Pipe {
	
	public String mask;
	
	public FeatureSelection() {
		addErrorCheck("mask", new ErrorCheck<String>() {
			@Override public String getError(String value) {
				if (Utils.count(mask, '0') + Utils.count(mask, '1') != mask.length())
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
		return Utils.count(mask, '1');
	}

	@Override
	public FeatureType getFeatureType(int featureIndex) {
		FeatureType ret = FeatureType.NUMERIC;
		if (getParent(0) == null)
			return ret;
		int parentFeatureIndex = 0;
		for(int i = 0; i < featureIndex; i++) {
			parentFeatureIndex++;
			if (mask.charAt(i) == '0')
				i--;
		}
		return getParent(0).getFeatureType(parentFeatureIndex);
	}

}
