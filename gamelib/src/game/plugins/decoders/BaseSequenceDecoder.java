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
import game.plugins.constraints.CompatibleWith;
import game.plugins.encoders.BaseSequenceEncoder;

import java.util.LinkedList;
import java.util.List;

public class BaseSequenceDecoder extends Decoder<BaseSequenceEncoder> {
	
	public Decoder atomDecoder;
	
	public boolean interpolate = false;
	
	public BaseSequenceDecoder() {
		setOptionBinding("encoder.atomEncoder", "atomDecoder.encoder");
		
		setOptionConstraint("atomDecoder", new CompatibleWith(this, "encoder.atomEncoder"));
	}

	@Override
	public Object decode(Encoding outputEncoded) {
		List ret = new LinkedList<>();
		outputEncoded = interpolate ? outputEncoded.makeInterpolatedEncoding(encoder.windowSize)
									: outputEncoded.makeTrimmedEncoding(encoder.windowSize);
		for (double[] enc: outputEncoded)
			ret.add(atomDecoder.decode(new Encoding(enc)));
		return ret;
	}

	@Override
	public boolean isCompatible(Encoder object) {
		return object instanceof BaseSequenceEncoder;
	}

}
