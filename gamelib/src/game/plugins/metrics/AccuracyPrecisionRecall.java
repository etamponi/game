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
package game.plugins.metrics;

import game.core.DataTemplate;
import game.core.Dataset;
import game.core.Experiment;
import game.core.Instance;
import game.core.metrics.FullMetric;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.datatemplates.SequenceTemplate;

import java.util.LinkedList;
import java.util.List;

public class AccuracyPrecisionRecall extends FullMetric {
	
	public List<Double> singleTP = new LinkedList<>();
	public List<Double> singleT = new LinkedList<>();
	public List<Double> singleP = new LinkedList<>();
	
	public boolean ready = false;
	
	public AccuracyPrecisionRecall() {
		setInternalOptions("singleTP", "singleT", "singleP", "ready");
	}
	
	@Override
	public boolean isCompatible(Experiment exp) {
		return super.isCompatible(exp) &&
				isCompatible(exp.template.outputTemplate);
	}
	
	private boolean isCompatible(DataTemplate template) {
		return template instanceof LabelTemplate ||
				(template instanceof SequenceTemplate && template.getOption("atom") instanceof LabelTemplate);
	}

	@Override
	public void evaluate(Dataset... folds) { // FIXME Use folds instead of only one dataset
		if (isReady())
			return;
		
		Dataset dataset = mergeFolds(folds);
		List<String> labels = getLabels(experiment.template.outputTemplate);
		
		for(int k = 0; k < labels.size(); k++) {
			singleTP.add(0.0);
			singleP.add(0.0);
			singleT.add(0.0);
		}
		
		for(Instance i: dataset) {
			List<String> trueLabels = getData(i.getOutputData());
			List<String> predLabels = getData(i.getPredictedData());
			for (int index = 0; index < trueLabels.size(); index++) {
				String trueLabel = trueLabels.get(index);
				String predLabel = predLabels.get(index);
				int k = labels.indexOf(predLabel);
				singleP.set(k, singleP.get(k)+1);
				if (predLabel.equals(trueLabel))
					singleTP.set(k, singleTP.get(k)+1);
				k = labels.indexOf(trueLabel);
				singleT.set(k, singleT.get(k)+1);
			}
		}
		
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
		
		ret.append(String.format("%20s%15s%15s%15s%15s%15s\n", "", "Observed", "Classified", "Correct", "Precision", "Recall"));
		List<String> labels = getLabels(experiment.template.outputTemplate);
		double totalT = 0;
		double totalTP = 0;
		for(int i = 0; i < labels.size(); i++) {
			ret.append(String.format("%20s%15.0f%15.0f%15.0f%15.2f%15.2f\n", labels.get(i),
																	   		 singleT.get(i),
																	   		 singleP.get(i),
																	   		 singleTP.get(i),
																	   		 singleTP.get(i)/singleP.get(i)*100,
																	   		 singleTP.get(i)/singleT.get(i)*100));
			totalT += singleT.get(i);
			totalTP += singleTP.get(i);
		}
		ret.append(String.format("\n%20s%15.0f%15.0f%15.0f%15s%15.2f\n", "Overall",
																	 totalT, totalT, totalTP,
																	 "Accuracy", 100*totalTP/totalT));
		
		return ret.toString();
	}

}
