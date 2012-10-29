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
package game.plugins.classifiers;

import game.core.DataTemplate.Data;
import game.core.Encoding;
import game.core.InstanceTemplate;
import game.core.blocks.Combiner;

import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;

public class AveragingCombiner extends Combiner {

	@Override
	public boolean isCompatible(InstanceTemplate object) {
		return true;
	}

	@Override
	public Encoding transform(Data input) {
		List<Encoding> encs = getParentsEncodings(input);
		
		if (encs.size() == 1)
			return encs.get(0);
		
		RealMatrix ret = new Encoding(getFeatureNumber(), input.size());
		double normalization = 1.0 / encs.size();
		
		for(Encoding enc: encs)
			ret = ret.add(enc);
		ret = ret.scalarMultiply(normalization);
		
		return new Encoding(ret);
	}

	@Override
	public FeatureType getFeatureType(int featureIndex) {
		return FeatureType.NUMERIC;
	}

}
