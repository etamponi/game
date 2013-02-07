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
import game.core.TrainingAlgorithm;
import game.core.blocks.DataFeeder;
import game.plugins.blocks.classifiers.KNNClassifier;
import game.plugins.blocks.classifiers.KNNClassifier.ReferenceSample;
import game.plugins.blocks.pipes.LabelToOneHot;
import game.plugins.datatemplates.LabelTemplate;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.linear.RealVector;

import com.ios.triggers.MasterSlaveTrigger;

public class SimpleKNNTraining extends TrainingAlgorithm<KNNClassifier> {
	
	public SimpleKNNTraining() {
		addTrigger(new MasterSlaveTrigger(this, "block.datasetTemplate.targetTemplate.0.labels", "block.outputTemplate.0.dimension") {
			@Override protected Object transform(final Object content) {
				return content == null ? 0 : ((List)content).size();
			}
		});
	}

	@Override
	protected void train(Dataset trainingSet) {
		LabelToOneHot outputFilter = new LabelToOneHot();
		outputFilter.parents.add(new DataFeeder(false));
		outputFilter.parents.setContent("0.datasetTemplate", block.datasetTemplate);
		Iterator<Sample> it = trainingSet.sampleIterator(block.getParent(), outputFilter);
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
		return template.targetTemplate.isSingletonTemplate(LabelTemplate.class);
	}

}
