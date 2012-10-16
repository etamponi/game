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
import game.core.Encoding;
import game.core.blocks.Pipe;

import java.util.List;

public class WindowEnlarger extends Pipe {
	
	public int windowSize = 1;
	
	public WindowEnlarger() {
		setOptionChecks("parents", new SizeCheck(1, 1));
		
		setOptionChecks("windowSize", new PositivenessCheck(false));
	}

	@Override
	public Encoding transform(List input) {
		return getParent(0).transform(input).makeWindowedEncoding(windowSize);
	}

	@Override
	public int getFeatureNumber() {
		if (parents.isEmpty())
			return 0;
		else
			return getParent(0).getFeatureNumber()*windowSize;
	}

}
