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
package game.core.experiments;

import game.core.Data;
import game.core.Dataset;
import game.core.DatasetTemplate;
import game.core.Experiment;
import game.core.Instance;
import game.core.blocks.Classifier;
import game.core.blocks.Decoder;
import game.plugins.valuetemplates.LabelTemplate;

import java.util.Iterator;

import com.ios.errorchecks.PropertyCheck;
import com.ios.triggers.MasterSlaveTrigger;

public abstract class ClassificationExperiment extends Experiment<ClassificationResult> {
	
	public Classifier classifier;
	
	public ClassificationExperiment() {
		addTrigger(new MasterSlaveTrigger(this, "datasetBuilder.datasetTemplate", "classifier.datasetTemplate"));
		
		addErrorCheck(new PropertyCheck<DatasetTemplate>("datasetBuilder.datasetTemplate") {
			@Override
			protected String getError(DatasetTemplate value) {
				if (value.targetTemplate.isSingletonTemplate(LabelTemplate.class))
					return null;
				else
					return "set a single LabelTemplate as target";
			}
		});
		
		addErrorCheck(new PropertyCheck<Decoder>("classifier.decoder") {
			@Override public String getError(Decoder value) {
				if (value == null)
					return "select a valid Decoder";
				else
					return null;
			}
		});
	}
	
	protected Dataset classifyDataset(double finalPercent, Classifier cls, Dataset dataset) {
		Dataset ret = new Dataset(dataset.getTemplate());
		double startPercent = getProgress();
		double increase = (finalPercent - startPercent) / dataset.size();
		int count = 1;
		Iterator<Instance> it = dataset.iterator();
		while (it.hasNext()) {
			Instance instance = it.next();
			ret.add(classify(instance, cls));
			if (count % 10 == 0 || count == dataset.size())
				updateStatus(startPercent+count*increase, "instances predicted " + count + "/" + dataset.size());
			count++;
		}
		return ret;
	}
	
	private Instance classify(Instance instance, Classifier cls) {
		Instance ret = new Instance(instance.getSource(), instance.getTarget());
		Data output = cls.transform(instance.getSource());
		ret.setPrediction(output);
		return ret;
	}

}
