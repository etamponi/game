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
package game.plugins.trainingalgorithms;

import game.core.Dataset;
import game.core.DatasetTemplate;
import game.core.Sample;
import game.core.trainingalgorithms.ClassifierTrainingAlgorithm;
import game.plugins.blocks.classifiers.KNNClassifier;
import game.plugins.blocks.classifiers.KNNClassifier.ReferenceSample;
import game.plugins.blocks.filters.LabelToOneHot;

import java.util.Iterator;

import org.apache.commons.math3.linear.RealVector;

public class SimpleKNNTraining extends ClassifierTrainingAlgorithm<KNNClassifier> {

	@Override
	protected void train(Dataset trainingSet) {
		LabelToOneHot outputFilter = new LabelToOneHot();
		
		outputFilter.setContent("datasetTemplate", trainingSet.getTemplate().reverseTemplate());
		
		Iterator<Sample> it = trainingSet.sampleIterator(null, outputFilter, null);
		while(it.hasNext()) {
			Sample sample = it.next();
			block.reference.add(new ReferenceSample(
					sample.getSource().get(RealVector.class),
					sample.getTarget().get(RealVector.class)));
		}
	}

	@Override
	protected String getTrainingPropertyNames() {
		return "reference";
	}

	@Override
	protected boolean isCompatible(DatasetTemplate template) {
		return true;
	}

}
