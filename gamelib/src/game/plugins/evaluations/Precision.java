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

import game.core.DataTemplate;
import game.core.Dataset;
import game.core.Evaluation;
import game.core.Experiment;
import game.core.Instance;
import game.core.experiments.FullExperiment;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.datatemplates.SequenceTemplate;

import java.util.LinkedList;
import java.util.List;

public class Precision extends Evaluation {
	
	public List<Double> precisionPerLabel = new LinkedList<>();
	
	public double overallPrecision = 0;
	
	public boolean ready = false;
	
	public Precision() {
		setInternalOptions("precisionPerLabel", "overallPrecision", "ready");
	}
	
	@Override
	public boolean isCompatible(Experiment experiment) {
		return experiment instanceof FullExperiment &&
				isCompatible(experiment.template.outputTemplate);
	}
	
	private boolean isCompatible(DataTemplate template) {
		return template instanceof LabelTemplate ||
				(template instanceof SequenceTemplate && template.getOption("atom") instanceof LabelTemplate);
	}

	@Override
	public void evaluate(Dataset... folds) { // FIXME Use folds instead of only one dataset
		Dataset dataset = folds[0];
		List<String> labels = getLabels(experiment.template.outputTemplate);
		
		List<Double> singleTP = new LinkedList<>();
		List<Double> singleP = new LinkedList<>();
		for(int k = 0; k < labels.size(); k++) {
			singleTP.add(0.0);
			singleP.add(0.0);
		}
		
		double TP = 0;
		int count = 0;
		
		for(Instance i: dataset) {
			List<String> trueLabels = getData(i.getOutputData());
			List<String> predLabels = getData(i.getPredictedData());
			for (int index = 0; index < trueLabels.size(); index++) {
				String trueLabel = trueLabels.get(index);
				String predLabel = predLabels.get(index);
				int k = labels.indexOf(predLabel);
				singleP.set(k, singleP.get(k)+1);
				if (predLabel.equals(trueLabel)) {
					singleTP.set(k, singleTP.get(k)+1);
					TP++;
				}
			}
			count += trueLabels.size();
		}
		
		for(int k = 0; k < labels.size(); k++) {
			precisionPerLabel.add(singleTP.get(k)/singleP.get(k));
		}
		overallPrecision = TP / count;
		
		ready = true;
	}
	
	private List<String> getLabels(DataTemplate template) {
		if (template instanceof SequenceTemplate)
			return template.getOption("atom.labels");
		else
			return template.getOption("labels");
	}
	
	private List<String> getData(Object data) {
		if (data instanceof List)
			return (List<String>)data;
		else {
			List<String> ret = new LinkedList<>();
			ret.add((String)data);
			return ret;
		}
	}

	@Override
	public boolean isReady() {
		return ready;
	}

	@Override
	public String prettyPrint() {
		StringBuilder ret = new StringBuilder();
		
		ret.append("Per label precision: ");
		for (String label: getLabels(experiment.template.outputTemplate))
			ret.append(String.format("%20s", label));
		ret.append("    -----    Overall\n");
		ret.append("                     ");
		for (double p: precisionPerLabel)
			ret.append(String.format("%19.2f%%", 100*p));
		ret.append(String.format("%19.2f%%", 100*overallPrecision));
		
		return ret.toString();
	}

}
