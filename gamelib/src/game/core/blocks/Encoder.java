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

import game.configuration.errorchecks.CompatibilityCheck;
import game.configuration.errorchecks.SizeCheck;
import game.core.Block;
import game.core.Dataset;
import game.core.DataTemplate;
import game.plugins.constraints.Compatible;

public abstract class Encoder<DT extends DataTemplate> extends Block implements Compatible<DataTemplate> {
	
	public DT template;
	
	public Encoder() {
		setOptionChecks("parents", new SizeCheck(0, 0));
		setOptionChecks("template", new CompatibilityCheck(this));
	}

	@Override
	public boolean isTrained() {
		return true;
	}

	@Override
	protected void train(Dataset trainingSet) {
		throw new UnsupportedOperationException("You cannot train an Encoder!");
	}
	
	@Override
	public boolean acceptsNewParents() {
		return false;
	}

}
