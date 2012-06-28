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
package game.plugins.evaluations;

import game.core.Dataset;
import game.core.Evaluation;
import game.core.Experiment;
import game.core.Instance;
import game.core.experiments.FullExperiment;
import game.plugins.datatemplates.LabelTemplate;

import java.util.LinkedList;
import java.util.List;

public class Recall extends Evaluation {

	public List<Double> recallPerLabel = new LinkedList<>();
	
	public double overallRecall = 0;
	
	public boolean ready;
	
	public Recall() {
		setInternalOptions("recallPerLabel", "overallRecall", "ready");
	}
	
	@Override
	public boolean isCompatible(Experiment experiment) {
		return experiment instanceof FullExperiment &&
				experiment.template.outputTemplate instanceof LabelTemplate;
	}

	@Override
	public void evaluate(Dataset... folds) { // FIXME Use folds instead of only one dataset
		Dataset dataset = folds[0];
		List<String> labels = experiment.template.getOption("outputTemplate.labels");
		
		List<Double> singleTP = new LinkedList<>();
		List<Double> singleT = new LinkedList<>();
		for(int k = 0; k < labels.size(); k++) {
			singleTP.add(0.0);
			singleT.add(0.0);
		}
		
		double TP = 0;
		
		for(Instance i: dataset) {
			String trueLabel = (String)i.getOutputData();
			String predLabel = (String)i.getPredictedData();
			int k = labels.indexOf(trueLabel);
			singleT.set(k, singleT.get(k)+1);
			if (trueLabel.equals(predLabel)) {
				singleTP.set(k, singleTP.get(k)+1);
				TP++;
			}
		}
		
		for(int k = 0; k < labels.size(); k++) {
			recallPerLabel.add(singleTP.get(k)/singleT.get(k));
		}
		overallRecall = TP / dataset.size();
		
		ready = true;
	}

	@Override
	public boolean isReady() {
		return ready;
	}

	@Override
	public String prettyPrint() {
		StringBuilder ret = new StringBuilder();
		
		ret.append("Per label recall: ");
		for (String label: (List<String>)experiment.template.getOption("outputTemplate.labels"))
			ret.append(String.format("%20s", label));
		ret.append("    -----    Overall\n");
		ret.append("                  ");
		for (double p: recallPerLabel)
			ret.append(String.format("%19.2f%%", 100*p));
		ret.append(String.format("%19.2f%%", 100*overallRecall));
		
		return ret.toString();
	}
	
}
