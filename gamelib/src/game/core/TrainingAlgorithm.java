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

import java.lang.reflect.ParameterizedType;

import com.ios.Compatible;
import com.ios.Property;
import com.ios.errorchecks.CompatibilityCheck;
import com.ios.listeners.SubPathListener;
import com.ios.triggers.SimpleTrigger;

public abstract class TrainingAlgorithm<B extends Block> extends LongTask<Void, Dataset> implements Compatible<Block> {
	
	public B block;
	
	public TrainingAlgorithm() {
		addErrorCheck("block", new CompatibilityCheck(this));
		
		addTrigger(new SimpleTrigger(new SubPathListener(new Property(this, "block.trainingAlgorithm"))) {
			private TrainingAlgorithm self = TrainingAlgorithm.this;
			@Override
			public void action(Property changedPath) {
				if (self != self.block.trainingAlgorithm) {
					self.setContent("block", null);
				}
			}
		});
	}
	
	protected abstract void train(Dataset dataset);
	
	protected abstract String getTrainingPropertyNames();
	
	protected abstract boolean isCompatible(DatasetTemplate datasetTemplate);

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

	public String[] getTrainingProperties() {
		if (!getTrainingPropertyNames().isEmpty())
			return getTrainingPropertyNames().split(" ");
		else
			return new String[0];
	}

	@Override
	public boolean isCompatible(Block block) {
		if (!((Class)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0]).isAssignableFrom(block.getClass()))
			return false;
		else
			return isCompatible(block.datasetTemplate);
	}

}
