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
import game.core.Instance;
import game.core.Result;
import game.core.metrics.FullMetric;
import game.plugins.datatemplates.ProteinDSSPStructure;
import game.plugins.datatemplates.ProteinHECStructure;
import game.plugins.datatemplates.ProteinPrimaryStructure;

import java.util.Iterator;
import java.util.List;

public class PerProteinStatistics extends FullMetric {
	
	@Override
	public boolean isCompatible(Result result) {
		return super.isCompatible(result) && 
				(result.experiment.template.outputTemplate instanceof ProteinHECStructure ||
				 result.experiment.template.outputTemplate instanceof ProteinDSSPStructure);
	}

	@Override
	protected void prepare() {
		
	}

	@Override
	public String prettyPrint() {
		StringBuilder ret = new StringBuilder();
		for (Dataset dataset: getResult().testedDatasets) {
			Iterator<Instance> it = dataset.iterator();
			while(it.hasNext()) {
				Instance i = it.next();
				if (getResult().experiment.template.inputTemplate instanceof ProteinPrimaryStructure)
					ret.append("Primary:          ").append(i.getInput()).append("\n");
				ret.append(	   "Secondary (obs):  ").append(i.getOutput()).append("\n");
				ret.append(    "Secondary (pred): ").append(i.getPrediction()).append("\n\n");
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
