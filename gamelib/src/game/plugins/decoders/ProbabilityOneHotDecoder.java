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
import game.plugins.encoders.OneHotEncoder;

import org.apache.commons.math3.linear.RealVector;

public class ProbabilityOneHotDecoder extends Decoder<OneHotEncoder> {

	@Override
	protected LabelData baseDecode(Encoding outputEncoded) {
		LabelData ret = encoder.template.newDataInstance();
		
		for (RealVector element: outputEncoded) {
			ret.add(encoder.template.labels.get(element.getMaxIndex(), String.class));
		}
		
		return ret;
	}

	@Override
	public boolean isCompatible(Encoder object) {
		return object instanceof OneHotEncoder;
	}

}
