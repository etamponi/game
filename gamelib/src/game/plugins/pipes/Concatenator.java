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

import game.core.Encoding;
import game.core.blocks.Pipe;

import java.util.List;

public class Concatenator extends Pipe {

	@Override
	public Encoding transform(Object inputData) {
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
			ret.add(element);
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
