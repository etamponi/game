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

import com.ios.Compatible;
import com.ios.errorchecks.CompatibilityCheck;

public abstract class TrainingAlgorithm<B extends Block> extends LongTask<Void, Dataset> implements Compatible<Block> {
	
	public B block;
	
	public TrainingAlgorithm() {
		addErrorCheck("block", new CompatibilityCheck(this));
	}
	
	protected abstract void train(Dataset dataset);
	
	protected abstract String getManagedPropertyNames();

	@Override
	public Void execute(Dataset dataset) {
		if (!block.trained) {
			updateStatus(0.0, "training " + block + " using " + this.getClass().getSimpleName());
			train(dataset);
			updateStatus(1.0, "training of " + block + " finished");
		}
		block.trained = true;
		
		return null;
	}

	public String[] getManagedProperties() {
		if (!getManagedPropertyNames().isEmpty())
			return getManagedPropertyNames().split(" ");
		else
			return new String[0];
	}

}
