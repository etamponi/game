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
import game.plugins.encoders.OneHotEncoder;

public class ProbabilityLabelDecoder extends Decoder<OneHotEncoder> {

	@Override
	public Object decode(Encoding outputEncoded) {
		double[] enc = outputEncoded.get(0);
		int maxIndex = 0;
		double maxProb = enc[0];
		for(int i = 1; i < enc.length; i++) {
			if (enc[i] > maxProb) {
				maxProb = enc[i];
				maxIndex = i;
			}
		}
		return encoder.template.labels.get(maxIndex);
	}

	@Override
	public boolean isCompatible(Encoder object) {
		return object instanceof OneHotEncoder;
	}

}
