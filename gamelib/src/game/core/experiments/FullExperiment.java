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
import game.core.Dataset.InstanceIterator;
import game.core.Experiment;
import game.core.Instance;
import game.core.blocks.Graph;

import java.util.ArrayList;
import java.util.List;

public abstract class FullExperiment extends Experiment {
	
	public Graph graph;
	
	public List<Dataset> testedDatasets = new ArrayList<>();
	
	public List<Graph> trainedGraphs = new ArrayList<>();
	
	public FullExperiment() {
		setOptionBinding("template", "graph.template");
		
		setPrivateOptions("testedDatasets", "trainedGraphs");
	}
	
	protected Dataset classifyDataset(double finalPercent, Graph graphClone, Dataset dataset, String outputDirectory) {
		Dataset ret = new Dataset(outputDirectory, false);
		double singleIncrease = (getCurrentPercent() - finalPercent) / dataset.size();
		int count = 1;
		InstanceIterator it = dataset.instanceIterator();
		while (it.hasNext()) {
			Instance instance = it.next();
			graphClone.classifyInstance(instance);
			ret.add(instance);
			if ((count-1) % 10 == 0 || count == dataset.size())
				updateStatus(getCurrentPercent()+singleIncrease, "instances predicted " + count + "/" + dataset.size());
			count++;
		}
		ret.setReadOnly();
		return ret;
	}

}
