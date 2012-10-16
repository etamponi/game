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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.RealVector;

public class ProbabilityOneHotDecoder extends Decoder<OneHotEncoder> {

	@Override
	protected List baseDecode(Encoding outputEncoded) {
		List ret = new ArrayList<>(outputEncoded.length());
		for (RealVector element: outputEncoded) {
			ret.add(encoder.template.labels.get(element.getMaxIndex()));
		}
		return ret;
	}

	@Override
	public boolean isCompatible(Encoder object) {
		return object instanceof OneHotEncoder;
	}

}
