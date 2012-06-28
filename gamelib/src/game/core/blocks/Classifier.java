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
package game.core.blocks;

import game.configuration.errorchecks.SizeCheck;
import game.core.Block;
import game.core.Encoding;


public abstract class Classifier extends Transducer {
	
	public Classifier() {
		setOptionChecks("parents", new SizeCheck(1, 1));
	}
	
	protected abstract Encoding classify(Encoding inputEncoded);

	@Override
	protected Encoding transform(Object inputData) {
		return classify(getParentEncoding(0, inputData));
	}
	
	protected Block getParent() {
		return (Block)parents.get(0);
	}

}
