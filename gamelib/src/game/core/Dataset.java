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
package game.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Dataset extends ArrayList<Instance> {

	private static final long serialVersionUID = -5878467744412002806L;
	
	public List<EncodedSample> encode(Block inputEncoder, Block outputEncoder) {
		List<EncodedSample> ret = new LinkedList<>();
		
		for(Instance i: this) {
			Encoding inputEncoding = inputEncoder.startTransform(i.getInputData());
			Encoding outputEncoding = outputEncoder.startTransform(i.getOutputData());
			
			for(int k = 0; k < inputEncoding.length(); k++) {
				int outputK = outputEncoding.length() == inputEncoding.length() ? k : 1;
				EncodedSample sample = new EncodedSample(inputEncoding.get(k), outputEncoding.get(outputK));
				ret.add(sample);
			}
		}
		
		return ret;
	}

}
