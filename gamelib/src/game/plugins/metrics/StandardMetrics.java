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

import game.core.Dataset;
import game.core.Dataset.SampleIterator;
import game.core.LabeledMatrix;
import game.core.Result;
import game.core.Sample;
import game.core.experiments.ClassificationResult;
import game.core.metrics.FullMetric;
import game.plugins.datatemplates.LabelTemplate;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.RealVector;

import com.ios.IList;

public class StandardMetrics extends FullMetric {
	
	private IList<String> labels;
	private List<Double> truePositives, falseNegatives;
	private List<Double> falsePositives, trueNegatives;
	
	@Override
	public boolean isCompatible(Result result) {
		return super.isCompatible(result) &&
				((ClassificationResult)result).classifiedDataset.getTemplate().targetTemplate.isSingletonTemplate(LabelTemplate.class);
	}

	@Override
	protected void prepareForEvaluation(ClassificationResult result) {	
		truePositives = new ArrayList<>();
		falsePositives = new ArrayList<>();
		falseNegatives = new ArrayList<>();
		trueNegatives = new ArrayList<>();
		
		Dataset dataset = result.classifiedDataset;
		
		labels = dataset.getTemplate().targetTemplate.getSingleton(LabelTemplate.class).labels.copy();
		for(int k = 0; k < labels.size(); k++) {
			truePositives.add(0.0);
			falsePositives.add(0.0);
			falseNegatives.add(0.0);
			trueNegatives.add(0.0);
		}
		
		SampleIterator it = dataset.sampleIterator();
		while(it.hasNext()) {
			Sample sample = it.next();
			String trueLabel = (String) sample.getTarget().get(0);
			String predLabel = (String) sample.getPrediction().get(0);
			int trueIndex = labels.indexOf(trueLabel);
			int predIndex = labels.indexOf(predLabel);
			
			if (predLabel.equals(trueLabel)) {
				// It is a "true positive" for the trueIndex and a "true negative" for ALL other indices
				for(int index = 0; index < labels.size(); index++) {
					if (index == trueIndex)
						truePositives.set(index, truePositives.get(index)+1);
					else
						trueNegatives.set(index, trueNegatives.get(index)+1);
				}
			} else {
				// It is a "false positive" for the predIndex and a "false negative" for the true index
				falsePositives.set(predIndex, falsePositives.get(predIndex)+1);
				falseNegatives.set(trueIndex, falseNegatives.get(trueIndex)+1);
			}
		}
	}

	@Override
	protected String getMetricNames() {
		return "precision recall accuracy fScore matthews";
	}
	
	private double evaluateWeightedAverage(RealVector row) {
		double ret = 0;
		double sum = 0;
		
		for(int i = 0; i < row.getDimension()-1; i++) {
			double w = truePositives.get(i) + falseNegatives.get(i);
			ret += row.getEntry(i) * w;
			sum += w;
		}
		ret = ret / sum;
		
		return ret;
	}
	
	protected LabeledMatrix evaluatePrecision(ClassificationResult result) {
		IList<String> labels = this.labels.copy();
		LabeledMatrix matrix = new LabeledMatrix(1, labels.size()+1);
		
		for(int i = 0; i < labels.size(); i++) {
			double TP = truePositives.get(i);
			double FP = falsePositives.get(i);
			matrix.setEntry(0, i, TP / (FP + TP));
		}
		
		matrix.setEntry(0, labels.size(), evaluateWeightedAverage(matrix.getRowVector(0)));
		labels.add("w. avg");
		matrix.setColumnLabels(labels.toArray(new String[labels.size()]));
		matrix.setRowLabels("Precision");
		
		return matrix;
	}
	
	protected LabeledMatrix evaluateRecall(ClassificationResult result) {
		IList<String> labels = this.labels.copy();
		LabeledMatrix matrix = new LabeledMatrix(1, labels.size()+1);
		
		for(int i = 0; i < labels.size(); i++) {
			double TP = truePositives.get(i);
			double FN = falseNegatives.get(i);
			matrix.setEntry(0, i, TP / (FN + TP));
		}

		matrix.setEntry(0, labels.size(), evaluateWeightedAverage(matrix.getRowVector(0)));
		labels.add("w. avg");
		matrix.setRowLabels("Recall");
		matrix.setColumnLabels(labels.toArray(new String[labels.size()]));
		
		return matrix;
	}
	
	protected LabeledMatrix evaluateAccuracy(ClassificationResult result) {
		IList<String> labels = this.labels.copy();
		LabeledMatrix matrix = new LabeledMatrix(1, labels.size()+1);
		
		for(int i = 0; i < labels.size(); i++) {
			double TP = truePositives.get(i);
			double TN = trueNegatives.get(i);
			double FN = falseNegatives.get(i);
			double FP = falsePositives.get(i);
			matrix.setEntry(0, i, (TP + TN) / (FN + TP + FP + TN));
		}

		matrix.setEntry(0, labels.size(), evaluateWeightedAverage(matrix.getRowVector(0)));
		labels.add("w. avg");
		matrix.setRowLabels("Accuracy");
		matrix.setColumnLabels(labels.toArray(new String[labels.size()]));
		
		return matrix;
	}
	
	protected LabeledMatrix evaluateFScore(ClassificationResult result) {
		IList<String> labels = this.labels.copy();
		LabeledMatrix matrix = new LabeledMatrix(1, labels.size()+1);
		
		for(int i = 0; i < labels.size(); i++) {
			double TP = truePositives.get(i);
			double FN = falseNegatives.get(i);
			double FP = falsePositives.get(i);
			matrix.setEntry(0, i, TP / (FN + FP + TP));
		}

		matrix.setEntry(0, labels.size(), evaluateWeightedAverage(matrix.getRowVector(0)));
		labels.add("w. avg");
		matrix.setRowLabels("FScore");
		matrix.setColumnLabels(labels.toArray(new String[labels.size()]));
		
		return matrix;
	}
	
	protected LabeledMatrix evaluateMatthews(ClassificationResult result) {
		IList<String> labels = this.labels.copy();
		LabeledMatrix matrix = new LabeledMatrix(1, labels.size()+1);
		
		for(int i = 0; i < labels.size(); i++) {
			double TP = truePositives.get(i);
			double FN = falseNegatives.get(i);
			double FP = falsePositives.get(i);
			double TN = trueNegatives.get(i);
			
			double P = TP + FP;
			double N = FN + TN;
			double T = TP + FN;
			double F = FP + TN;
			double den = Math.sqrt(P * N * T * F);
			
			if (den == 0)
				matrix.setEntry(0, i, 0);
			else
				matrix.setEntry(0, i, (TP*TN - FP*FN) / den);
		}

		matrix.setEntry(0, labels.size(), evaluateWeightedAverage(matrix.getRowVector(0)));
		labels.add("w. avg");
		matrix.setRowLabels("Matthews");
		matrix.setColumnLabels(labels.toArray(new String[labels.size()]));
		
		return matrix;
	}
}
