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

import game.configuration.Change;
import game.core.Block;
import game.editorsystem.Option;
import game.plugins.editors.ConfigurableEditor;

import java.util.Observable;

public class BlockEditor extends ConfigurableEditor {

	public BlockEditor() {
		setHiddenOptions("parents", "position");
	}

	@Override
	public Class getBaseEditableClass() {
		return Block.class;
	}

	@Override
	public void connect(Option model) {
		/*
		if (model.getContent() != null) {
			if (((Block)model.getContent()).trained)
				setReadOnly(true);
		}
		*/
		super.connect(model);
	}
	
	@Override
	public void update(Observable observed, Object message) {
		super.update(observed, message);
		if (message instanceof Change) {
			if (((Change)message).getPath().matches("(.)+\\.trainingAlgorithm"))
				updateView();
		}
	}
	
}
