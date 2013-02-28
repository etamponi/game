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
import com.ios.errorchecks.CompatibilityCheck;

public abstract class TrainingAlgorithm<B extends Block> extends LongTask<Void, Dataset> implements Compatible<Block> {
	
	public B block;
	
	public TrainingAlgorithm() {
		addErrorCheck(new CompatibilityCheck("block"));
	}
	
	protected abstract void train(Dataset dataset);
	
	protected abstract String getTrainingPropertyNames();
	
	protected abstract String compatibilityError(DatasetTemplate datasetTemplate);

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
		if (getTrainingPropertyNames() != null && !getTrainingPropertyNames().isEmpty())
			return getTrainingPropertyNames().split(" ");
		else
			return new String[0];
	}

	@Override
	public String compatibilityError(Block block) {
		Class type = (Class)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		if (!type.isAssignableFrom(block.getClass()))
			return "is compatible only with " + type.getSimpleName();
		else
			return (block.datasetTemplate == null || !block.datasetTemplate.isReady()) ?
					"datasetTemplate is null or is not ready"
					: compatibilityError(block.datasetTemplate);
	}

}
