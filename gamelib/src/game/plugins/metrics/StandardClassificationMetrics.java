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
import game.core.Metrics;
import game.core.Result;
import game.core.Sample;
import game.core.blocks.Decoder;
import game.core.experiments.ClassificationResult;
import game.plugins.valuetemplates.LabelTemplate;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.RealVector;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.ios.IList;

public class StandardClassificationMetrics extends Metrics<ClassificationResult> {

	private IList<String> labels;
	private List<Double> truePositives, falseNegatives;
	private List<Double> falsePositives, trueNegatives;
	
	@Override
	protected void prepareForEvaluation(ClassificationResult result) {	
		truePositives = new ArrayList<>();
		falsePositives = new ArrayList<>();
		falseNegatives = new ArrayList<>();
		trueNegatives = new ArrayList<>();
		
		Dataset dataset = result.classifiedDataset;
		Decoder decoder = result.trainedClassifier.decoder;
		
		labels = dataset.getTemplate().targetTemplate.getSingleton(LabelTemplate.class).labels.copy();
		for(int k = 0; k < labels.size(); k++) {
			truePositives.add(0.0);
			falsePositives.add(0.0);
			falseNegatives.add(0.0);
			trueNegatives.add(0.0);
		}
		
		SampleIterator it = dataset.sampleIterator(null, null, decoder);
		while(it.hasNext()) {
			Sample sample = it.next();
			String trueLabel = (String) sample.getTarget().get();
			String predLabel = (String) sample.getDecodedTarget().get();
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
	
	private static final String[] rows = {"Precision", "Recall", "Accuracy", "FScore", "Matthews"};
	protected LabeledMatrix evaluateMetrics(ClassificationResult result) {
		IList<String> labels = this.labels.copy();
		LabeledMatrix matrix = new LabeledMatrix(rows.length, labels.size()+1);
		
		MethodAccess access = MethodAccess.get(StandardClassificationMetrics.class);
		for(int row = 0; row < rows.length; row++) {
			matrix.getRowLabels().add(rows[row]);
			access.invoke(this, "evaluate"+rows[row], matrix, row, result);
			matrix.setEntry(row, labels.size(), evaluateWeightedAverage(matrix.getRowVector(row)));
		}

		labels.add("w. avg");
		matrix.setColumnLabels(labels.toArray(new String[labels.size()]));
		
		return matrix;
	}
	
	protected void evaluatePrecision(LabeledMatrix matrix, int row, ClassificationResult result) {
		for(int i = 0; i < labels.size(); i++) {
			double TP = truePositives.get(i);
			double FP = falsePositives.get(i);
			matrix.setEntry(row, i, TP / (FP + TP));
		}
	}
	
	protected void evaluateRecall(LabeledMatrix matrix, int row, ClassificationResult result) {
		for(int i = 0; i < labels.size(); i++) {
			double TP = truePositives.get(i);
			double FN = falseNegatives.get(i);
			matrix.setEntry(row, i, TP / (FN + TP));
		}
	}
	
	protected void evaluateAccuracy(LabeledMatrix matrix, int row, ClassificationResult result) {
		for(int i = 0; i < labels.size(); i++) {
			double TP = truePositives.get(i);
			double TN = trueNegatives.get(i);
			double FN = falseNegatives.get(i);
			double FP = falsePositives.get(i);
			matrix.setEntry(row, i, (TP + TN) / (FN + TP + FP + TN));
		}
	}
	
	protected void evaluateFScore(LabeledMatrix matrix, int row, ClassificationResult result) {
		for(int i = 0; i < labels.size(); i++) {
			double precision = matrix.getEntry(0, i);
			double recall = matrix.getEntry(1, i);
			matrix.setEntry(row, i, 2*(precision*recall)/(precision+recall));
		}
	}
	
	protected void evaluateMatthews(LabeledMatrix matrix, int row, ClassificationResult result) {
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
				matrix.setEntry(row, i, 0);
			else
				matrix.setEntry(row, i, (TP*TN - FP*FN) / den);
		}
	}

	public double getAccuracy() {
		return this.statistics.getMatrix()[2][labels.size()].getMean();
	}
	
	public String compatibilityError(Result result) {
		return result instanceof ClassificationResult ? null : "compatible with ClassificationResult only";
	}
	
}
