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
import game.core.blocks.Graph;

public abstract class FullExperiment extends Experiment {
	
	public Graph graph;
	
	public FullExperiment() {
		setOptionBinding("template", "graph.template");
	}
	
	protected Dataset classifyDataset(double finalPercent, Graph graphClone, Dataset dataset, String outputDirectory, String cacheName) {
		Dataset ret = new Dataset(template, outputDirectory, cacheName, false);
		double singleIncrease = (finalPercent - getCurrentPercent()) / dataset.size();
		int count = 1;
		InstanceIterator it = dataset.instanceIterator();
		while (it.hasNext()) {
			Instance instance = it.next();
			graphClone.classifyInstance(instance);
			ret.add(instance);
			if ((count-1) % 10 == 0 || count == dataset.size())
				updateStatus(getCurrentPercent()+10*singleIncrease, "instances predicted " + count + "/" + dataset.size());
			count++;
		}
		ret.setReadOnly();
		return ret;
	}

}
