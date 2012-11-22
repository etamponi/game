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
import game.core.blocks.PredictionGraph;

import java.util.Iterator;

import com.ios.triggers.MasterSlaveTrigger;

public abstract class FullExperiment extends Experiment {
	
	public PredictionGraph graph;
	
	public FullExperiment() {
		addTrigger(new MasterSlaveTrigger(this, "template", "graph.template"));
	}
	
	protected Dataset classifyDataset(double finalPercent, PredictionGraph graph, Dataset dataset) {
		Dataset ret = new Dataset(graph.template);
		double startPercent = getProgress();
		double increase = (finalPercent - startPercent) / dataset.size();
		int count = 1;
		Iterator<Instance> it = dataset.iterator();
		while (it.hasNext()) {
			Instance instance = it.next();
			graph.classifyInstance(instance);
			ret.add(instance);
			if ((count-1) % 10 == 0 || count == dataset.size())
				updateStatus(startPercent+count*increase, "instances predicted " + count + "/" + dataset.size());
			count++;
		}
		return ret;
	}

}
