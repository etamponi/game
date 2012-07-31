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
import game.plugins.encoders.VectorEncoder;

import java.util.ArrayList;
import java.util.List;

public class VectorDecoder extends Decoder<VectorEncoder> {

	@Override
	public boolean isCompatible(Encoder object) {
		return object instanceof VectorEncoder;
	}

	@Override
	protected List baseDecode(Encoding outputEncoded) {
		List ret = new ArrayList<>(outputEncoded.length());
		for(double[] element: outputEncoded)
			ret.add(element);
		return ret;
	}

}
