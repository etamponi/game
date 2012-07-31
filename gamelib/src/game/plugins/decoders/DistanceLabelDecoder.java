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
package game.plugins.decoders;

import game.core.Decoder;
import game.core.Encoding;
import game.core.blocks.Encoder;
import game.plugins.encoders.LabelEncoder;
import game.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class DistanceLabelDecoder extends Decoder<LabelEncoder> {

	@Override
	public List decode(Encoding outputEncoded) {
		List ret = new ArrayList<>(outputEncoded.length());
		
		for(double[] element: outputEncoded) {
			String label = null;
			double minDistance = Double.MAX_VALUE;
			for (Entry<String, double[]> entry: encoder.labelMapping.entrySet()) {
				double distance = Utils.getDistance(element, entry.getValue());
				if (distance < minDistance) {
					minDistance = distance;
					label = entry.getKey();
				}
			}
			ret.add(label);
		}
		
		return ret;
	}

	@Override
	public boolean isCompatible(Encoder object) {
		return object instanceof LabelEncoder;
	}

}
