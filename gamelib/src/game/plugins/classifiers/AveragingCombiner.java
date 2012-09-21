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
package game.plugins.classifiers;

import game.core.Encoding;
import game.core.InstanceTemplate;
import game.core.blocks.Combiner;

import java.util.List;

public class AveragingCombiner extends Combiner {

	@Override
	public boolean isCompatible(InstanceTemplate object) {
		return true;
	}

	@Override
	public Encoding transform(List input) {
		List<Encoding> encs = getParentsEncodings(input);
		
		if (encs.size() == 1)
			return encs.get(0);
		
		Encoding ret = new Encoding();
		double normalization = 1.0 / encs.size();
		
		for(Encoding enc: encs)
			sumEncodings(ret, enc);
		ret.mulBy(normalization);
		
		return ret;
	}
	
	protected void sumEncodings(Encoding to, Encoding from) {
		if (to.isEmpty()) {
			to.addAll(from);
			return;
		}
		for(int k = 0; k < to.length(); k++) {
			double[] element = to.get(k);
			double[] other = from.get(k);
			for (int i = 0; i < to.getElementSize(); i++)
				element[i] += other[i];
		}
	}

}
