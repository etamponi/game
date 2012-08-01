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

import java.util.List;

import game.configuration.ErrorCheck;
import game.configuration.errorchecks.CompatibilityCheck;
import game.configuration.errorchecks.PositivenessCheck;
import game.configuration.errorchecks.SizeCheck;
import game.core.Block;
import game.core.Dataset;
import game.core.DataTemplate;
import game.core.Encoding;
import game.plugins.constraints.Compatible;

public abstract class Encoder<DT extends DataTemplate> extends Block implements Compatible<DataTemplate> {
	
	public DT template;
	
	public int windowSize = 1;
	
	public Encoder() {
		setOptionChecks("parents", new SizeCheck(0, 0));
		setOptionChecks("template", new CompatibilityCheck(this));
		
		setOptionChecks("windowSize", new PositivenessCheck(false), new ErrorCheck<Integer>() {
			@Override
			public String getError(Integer value) {
				if (template != null && !template.sequence && windowSize != 1)
					return "has to be 1 (not a sequence)";
				else
					return null;
			}
		});
	}

	protected abstract Encoding baseEncode(List input);

	@Override
	public Encoding transform(List input) {
		return baseEncode(input).makeWindowedEncoding(windowSize);
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
