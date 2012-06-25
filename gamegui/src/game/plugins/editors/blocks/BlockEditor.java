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
package game.plugins.editors.blocks;

import game.core.Block;
import game.plugins.editors.ConfigurableEditor;

public class BlockEditor extends ConfigurableEditor {

	public BlockEditor() {
		setHiddenOptions("parents", "position");
	}

	@Override
	public Class getBaseEditableClass() {
		return Block.class;
	}
	
}
