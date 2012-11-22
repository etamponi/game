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
import game.plugins.datatemplates.LabelTemplate.LabelData;
import game.plugins.encoders.BooleanEncoder;

import org.apache.commons.math3.linear.RealVector;

import com.ios.errorchecks.RangeCheck;

public class ThresholdBooleanDecoder extends Decoder<BooleanEncoder> {
	
	public double threshold = 0.5;
	
	public ThresholdBooleanDecoder() {
		addErrorCheck("threshold", new RangeCheck(0.0, 1.0));
	}

	@Override
	public boolean isCompatible(Encoder object) {
		return object instanceof BooleanEncoder;
	}

	@Override
	protected LabelData baseDecode(Encoding outputEncoded) {
		LabelData ret = encoder.template.newData();
		for(RealVector element: outputEncoded) {
			double score = element.getEntry(0);
			if (score >= threshold)
				ret.add(encoder.positiveLabel());
			else
				ret.add(encoder.negativeLabel());
		}
		return ret;
	}

}
