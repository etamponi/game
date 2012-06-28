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
package game.plugins.pipes;

import game.configuration.errorchecks.PositivenessCheck;
import game.configuration.errorchecks.SizeCheck;
import game.core.Block;
import game.core.Encoding;
import game.core.blocks.Pipe;

public class WindowEnlarger extends Pipe {
	
	public int windowSize = 1;
	
	public WindowEnlarger() {
		setOptionChecks("parents", new SizeCheck(1, 1));
		
		setOptionChecks("windowSize", new PositivenessCheck(false));
	}

	@Override
	protected Encoding transform(Object inputData) {
		return parents.getList(Block.class).get(0).startTransform(inputData).makeWindowedEncoding(windowSize);
	}

}
