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

import game.core.Block;
import game.core.DataTemplate.Data;
import game.core.Encoding;
import game.core.blocks.Pipe;

import java.util.List;

public class Concatenator extends Pipe {

	@Override
	public Encoding transform(Data input) {
		List<Encoding> encs = getParentsEncodings(input);
		
		Encoding ret = new Encoding(getFeatureNumber(), input.size());
		
		int startRow = 0;
		for (Encoding enc: encs) {
			for (int i = 0; i < enc.length(); i++)
				ret.setSubMatrix(enc.getSubMatrix(0, enc.getFeatureNumber()-1, i, i).getData(), startRow, i);
			
			startRow += enc.getFeatureNumber();
		}
		
		return ret;
	}

	@Override
	public int getFeatureNumber() {
		int ret = 0;
		for (Block parent: parents)
			ret += parent.getFeatureNumber();
		return ret;
	}

	@Override
	public FeatureType getFeatureType(int featureIndex) {
		int count = 0;
		for (Block parent: parents) {
			if (parent.getFeatureNumber() + count > featureIndex)
				return parent.getFeatureType(featureIndex - count);
			else
				count += parent.getFeatureNumber();
		}
		return FeatureType.NUMERIC;
	}

}
