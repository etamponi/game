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
package game.core;

import com.ios.Compatible;
import com.ios.IObject;
import com.ios.errorchecks.CompatibilityCheck;

import game.core.DataTemplate.Data;
import game.core.blocks.Encoder;

public abstract class Decoder<E extends Encoder> extends IObject implements Compatible<Encoder> {
	
	public E encoder;
	
	public boolean interpolate = false;
	
	public Decoder() {
		addErrorCheck("encoder", new CompatibilityCheck(this));
	}
	
	protected abstract <D extends Data> D baseDecode(Encoding outputEncoded);
	
	public <D extends Data> D decode(Encoding outputEncoded) {
		if (interpolate)
			return baseDecode(outputEncoded.makeInterpolatedEncoding(encoder.windowSize));
		else
			return baseDecode(outputEncoded.makeTrimmedEncoding(encoder.windowSize, 1));
	}

}
