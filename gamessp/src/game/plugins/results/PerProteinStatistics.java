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
package game.plugins.results;

import game.core.Dataset;
import game.core.Dataset.InstanceIterator;
import game.core.DataTemplate;
import game.core.Experiment;
import game.core.Instance;
import game.core.experiments.FullExperiment;
import game.core.metrics.FullMetric;
import game.plugins.datatemplates.ProteinDSSPStructure;
import game.plugins.datatemplates.ProteinHECStructure;
import game.plugins.datatemplates.ProteinPrimaryStructure;

import java.util.List;

public class PerProteinStatistics extends FullMetric {
	
	public DataTemplate inputTemplate;
	public List<Dataset> testedDatasets;
	
	public PerProteinStatistics() {
		setPrivateOptions("inputTemplate", "dataset");
	}

	@Override
	public boolean isCompatible(Experiment object) {
		return super.isCompatible(object) && 
				(object.template.outputTemplate instanceof ProteinHECStructure ||
				 object.template.outputTemplate instanceof ProteinDSSPStructure);
	}

	@Override
	public boolean isReady() {
		return testedDatasets != null;
	}

	@Override
	public void evaluate(FullExperiment experiment) {
		testedDatasets = experiment.testedDatasets;
		inputTemplate = experiment.template.inputTemplate;
	}

	@Override
	public String prettyPrint() {
		StringBuilder ret = new StringBuilder();
		for (Dataset dataset: testedDatasets) {
			InstanceIterator it = dataset.instanceIterator();
			while(it.hasNext()) {
				Instance i = it.next();
				if (inputTemplate instanceof ProteinPrimaryStructure)
					ret.append("Primary:          ").append(getFasta(i.getInput())).append("\n");
				ret.append(	   "Secondary (obs):  ").append(getFasta(i.getOutput())).append("\n");
				ret.append(    "Secondary (pred): ").append(getFasta(i.getPrediction())).append("\n\n");
			}
		}
		
		return ret.toString();
	}
	
	public String getFasta(Object data) {
		StringBuilder ret = new StringBuilder();
		for(String e: (List<String>)data)
			ret.append(e);
		return ret.toString();
	}

}
