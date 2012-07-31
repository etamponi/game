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
import game.core.blocks.Encoder;
import game.plugins.constraints.Compatible;

import java.util.List;

public abstract class Decoder<E extends Encoder> extends Configurable implements Compatible<Encoder> {
	
	public E encoder;
	
	public Decoder() {
		setOptionChecks("encoder", new CompatibilityCheck(this));
	}
	
	public abstract List decode(Encoding outputEncoded);

}
