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
package game.plugins.decoders;

import game.core.Decoder;
import game.core.Encoding;
import game.core.blocks.Encoder;
import game.plugins.datatemplates.LabelTemplate.LabelData;
import game.plugins.encoders.LabelEncoder;
import game.utils.Utils;

import java.util.Map.Entry;

import org.apache.commons.math3.linear.RealVector;

public class DistanceLabelDecoder extends Decoder<LabelEncoder> {
	
	public String distanceType = "L2"; 

	@Override
	protected LabelData baseDecode(Encoding outputEncoded) {
		LabelData ret = encoder.template.newDataInstance();
		
		for(RealVector element: outputEncoded) {
			String label = null;
			double minDistance = Double.MAX_VALUE;
			for (Entry<String, RealVector> entry: encoder.labelMapping.entrySet()) {
				double distance = Utils.getDistance(distanceType, element, entry.getValue());
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
