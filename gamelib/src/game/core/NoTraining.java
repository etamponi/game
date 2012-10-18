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
package game.core;


public class NoTraining extends TrainingAlgorithm<Block> {

	@Override
	public boolean isCompatible(Block object) {
		return true;
	}

	@Override
	protected void train(Dataset dataset) {
		
	}

	@Override
	public String getTaskDescription() {
		return "no training for " + block;
	}

	@Override
	public String[] getBlockFixedOptions() {
		return new String[0];
	}

}
