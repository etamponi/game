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
package game.core.experiments;

import game.core.Dataset;
import game.core.Dataset.InstanceIterator;
import game.core.Experiment;
import game.core.Instance;
import game.core.blocks.PredictionGraph;

public abstract class FullExperiment extends Experiment {
	
	public PredictionGraph graph;
	
	public FullExperiment() {
		setOptionBinding("template", "graph.template");
	}
	
	protected Dataset classifyDataset(double finalPercent, PredictionGraph graphClone, Dataset dataset, String cacheFileName) {
		Dataset ret = new Dataset(template, cacheFileName);
		double startPercent = getCurrentPercent();
		double increase = (finalPercent - startPercent) / dataset.size();
		int count = 1;
		InstanceIterator it = dataset.instanceIterator();
		while (it.hasNext()) {
			Instance instance = it.next();
			graphClone.classifyInstance(instance);
			ret.add(instance);
			if ((count-1) % 10 == 0 || count == dataset.size())
				updateStatus(startPercent+count*increase, "instances predicted " + count + "/" + dataset.size());
			count++;
		}
		ret.setReadyState();
		return ret;
	}

}
