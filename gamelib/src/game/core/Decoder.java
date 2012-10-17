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

import game.configuration.Configurable;
import game.configuration.errorchecks.CompatibilityCheck;
import game.core.DataTemplate.Data;
import game.core.blocks.Encoder;
import game.plugins.constraints.Compatible;

public abstract class Decoder<E extends Encoder> extends Configurable implements Compatible<Encoder> {
	
	public E encoder;
	
	public boolean interpolate = false;
	
	public Decoder() {
		setOptionChecks("encoder", new CompatibilityCheck(this));
	}
	
	protected abstract <D extends Data> D baseDecode(Encoding outputEncoded);
	
	public <D extends Data> D decode(Encoding outputEncoded) {
		if (interpolate)
			return baseDecode(outputEncoded.makeInterpolatedEncoding(encoder.windowSize));
		else
			return baseDecode(outputEncoded.makeTrimmedEncoding(encoder.windowSize, 1));
	}

}
