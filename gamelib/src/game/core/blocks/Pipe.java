/*******************************************************************************
 * Copyright (c) 2012 Emanuele.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Emanuele - initial API and implementation
 ******************************************************************************/
package game.core.blocks;

import game.configuration.errorchecks.SizeCheck;
import game.core.Block;

public abstract class Pipe extends Block {
	
	public Pipe() {
		setOptionChecks("parents", new SizeCheck(1));
	}

	@Override
	public boolean acceptsParents() {
		return true;
	}

}
