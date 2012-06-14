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

import java.util.Map.Entry;

import game.core.Decoder;
import game.core.Encoding;
import game.plugins.encoders.LabelEncoder;

public class LabelDecoder extends Decoder<LabelEncoder> {

	@Override
	public Object decode(Encoding outputEncoded) {
		String ret = null;
		double[] enc = outputEncoded.get(0);
		double minDistance = Double.MAX_VALUE;
		
		for (Entry<String, double[]> entry: encoder.labelMapping.entrySet()) {
			double distance = getDistance(enc, entry.getValue());
			if (distance < minDistance) {
				minDistance = distance;
				ret = entry.getKey();
			}
		}
		
		return ret;
	}

	@Override
	public Class getBaseEncoderClass() {
		return LabelEncoder.class;
	}
	
	private double getDistance(double[] v1, double[] v2) {
		double ret = 0;
		for (int i = 0; i < v1.length; i++)
			ret += Math.pow(v1[i]-v2[i], 2);
		return Math.sqrt(ret);
	}

}
