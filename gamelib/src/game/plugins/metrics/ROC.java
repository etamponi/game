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

import game.configuration.errorchecks.RangeCheck;
import game.core.Experiment;
import game.core.experiments.FullExperiment;
import game.core.metrics.FullMetric;
import game.plugins.encoders.BooleanEncoder;

import java.util.LinkedList;
import java.util.List;

public class ROC extends FullMetric {

	public List<Double> TPs = new LinkedList<>();
	public List<Double> FPs = new LinkedList<>();
	
	public double P;
	public double N;
	
	public int steps = 20;
	
	public boolean ready = false;
	
	public ROC() {
		setOptionChecks("steps", new RangeCheck(RangeCheck.LOWER, 2));
		
		setPrivateOptions("TPs", "FPs", "P", "N", "ready");
	}

	@Override
	public boolean isCompatible(Experiment exp) {
		// TODO ROC supports Sequences too
		return super.isCompatible(exp) &&
				exp.getOption("graph.outputClassifier.outputEncoder") instanceof BooleanEncoder;
	}

	@Override
	public boolean isReady() {
		return ready;
	}

	@Override
	public void evaluate(FullExperiment experiment) {
		if (isReady())
			return;
		/*
		String positiveLabel = experiment.getOption("graph.outputClassifier.outputEncoder.positiveLabel");
		String negativeLabel = experiment.getOption("graph.outputClassifier.outputEncoder.negativeLabel");
		
		Dataset dataset = Utils.deepClone(mergeFolds(experiment.testedDatasets));
		P = getCount(dataset, positiveLabel);
		N = getCount(dataset, negativeLabel);
		
		double threshold;
		double step = 1.0/(steps-1);
		
		for(int i = 0; i < steps; i++) {
			threshold = i*step;
			decode(dataset, threshold, positiveLabel, negativeLabel);
			evaluateROCPoint(dataset, positiveLabel);
		}
		*/
		ready = true;
	}
	/*
	private void decode(Dataset dataset, double threshold, String positiveLabel, String negativeLabel) {
		for(Instance i: dataset) {
			double value = i.getPredictionEncoding().get(0)[0];
			if (value >= threshold)
				i.setPredictionData(positiveLabel);
			else
				i.setPredictionData(negativeLabel);
		}
	}
	
	private void evaluateROCPoint(Dataset dataset, String positiveLabel) {
		double TP = 0, FP = 0;
		
		for(Instance i: dataset) {
			if (i.getPredictionData().equals(positiveLabel)) {
				if (i.getPredictionData().equals(i.getOutputData()))
					TP++;
				else
					FP++;
			}
		}
		TPs.add(TP);
		FPs.add(FP);
	}
	
	private int getCount(Dataset dataset, String label) {
		int count = 0;
		
		for(Instance i: dataset) {
			if (i.getOutputData().equals(label))
				count++;
		}
		
		return count;
	}
*/
	@Override
	public String prettyPrint() {
		StringBuilder ret = new StringBuilder();
		
		ret.append("AUC: " + getAUC());
		
		return ret.toString();
	}
	
	private double getAUC() {
		double ret = 0;
		for(int i = 0; i < steps-1; i++) {
			ret += getTrapezoid(i);
		}
		return ret;
	}
	
	private double getTrapezoid(int i) {
		double TPR1 = TPs.get(i) / P;
		double FPR1 = FPs.get(i) / N;
		double TPR2 = TPs.get(i+1) / P;
		double FPR2 = FPs.get(i+1) / N;
		
		return Math.abs((TPR1+TPR2)*(FPR1-FPR2)/2);
	}

}
