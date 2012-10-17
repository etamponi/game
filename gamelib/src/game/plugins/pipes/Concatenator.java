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
			ret.setSubMatrix(enc.getData(), startRow, 0);
			startRow += enc.getFeatureNumber();
		}
		
		return ret;
	}

	@Override
	public int getFeatureNumber() {
		int ret = 0;
		for (Block parent: parents.getList(Block.class))
			ret += parent.getFeatureNumber();
		return ret;
	}

}
