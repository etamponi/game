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

import game.core.Dataset;
import game.core.Experiment;
import game.core.Instance;
import game.core.blocks.Graph;

import java.util.Iterator;

import com.ios.ErrorCheck;
import com.ios.triggers.MasterSlaveTrigger;

public abstract class ClassificationExperiment extends Experiment<ClassificationResult> {
	
	public Graph graph;
	
	public ClassificationExperiment() {
		addTrigger(new MasterSlaveTrigger(this, "datasetBuilder.datasetTemplate", "graph.datasetTemplate"));
		addErrorCheck("graph", new ErrorCheck<Graph>() {
			private ClassificationExperiment self = ClassificationExperiment.this;
			@Override
			public String getError(Graph value) {
				if (value == null || value.outputTemplate == null || self.getContent("datasetBuilder.datasetTemplate.targetTemplate") == null)
					return null;
				if (!value.outputTemplate.equals(self.datasetBuilder.datasetTemplate.targetTemplate))
					return "the graph should output data from " + self.datasetBuilder.datasetTemplate.targetTemplate;
				else
					return null;
			}
		});
	}
	
	protected Dataset classifyDataset(double finalPercent, Graph graph, Dataset dataset) {
		Dataset ret = new Dataset(graph.datasetTemplate);
		double startPercent = getProgress();
		double increase = (finalPercent - startPercent) / dataset.size();
		int count = 1;
		Iterator<Instance> it = dataset.iterator();
		while (it.hasNext()) {
			Instance instance = it.next();
			ret.add(graph.classify(instance));
			if (count % 10 == 0 || count == dataset.size())
				updateStatus(startPercent+count*increase, "instances predicted " + count + "/" + dataset.size());
			count++;
		}
		return ret;
	}

}
