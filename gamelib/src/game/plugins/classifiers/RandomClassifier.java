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
package game.plugins.classifiers;

import game.core.Block;
import game.core.Dataset;
import game.core.EncodedSample;
import game.core.Encoding;
import game.core.InstanceTemplate;
import game.core.blocks.Classifier;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.datatemplates.SequenceTemplate;

import java.util.List;

public class RandomClassifier extends Classifier {
	
	public List<EncodedSample> reference;
	
	public RandomClassifier() {
		setInternalOptions("reference");
	}

	@Override
	public boolean isCompatible(InstanceTemplate object) {
		return object.outputTemplate instanceof LabelTemplate ||
				(object.outputTemplate instanceof SequenceTemplate &&
				 object.getOption("outputTemplate.atom") instanceof LabelTemplate);
	}

	@Override
	public boolean isTrained() {
		return reference != null;
	}

	@Override
	protected void train(Dataset trainingSet) {
		reference = trainingSet.encode((Block)getParents().get(0), outputEncoder);
	}

	@Override
	protected Encoding classify(Encoding inputEncoded) {
		Encoding ret = new Encoding();
		for(int i = 0; i < inputEncoded.length(); i++)
			ret = new Encoding(reference.get((int)(Math.random() * (reference.size()-1))).getOutput());
		return ret;
	}
	
}
