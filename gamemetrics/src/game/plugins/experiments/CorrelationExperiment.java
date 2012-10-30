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
package game.plugins.experiments;

import game.configuration.ErrorCheck;
import game.configuration.errorchecks.RangeCheck;
import game.configuration.errorchecks.RangeCheck.RangeType;
import game.core.Block;
import game.core.Dataset;
import game.core.Dataset.SampleIterator;
import game.core.DatasetBuilder;
import game.core.Experiment;
import game.core.NoTraining;
import game.core.blocks.PredictionGraph;
import game.plugins.constraints.CompatibleWith;
import game.plugins.constraints.SubclassConstraint;
import game.plugins.correlation.CorrelationCoefficient;
import game.plugins.datatemplates.LabelTemplate;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class CorrelationExperiment extends Experiment {
	
	public DatasetBuilder dataset;
	
	public CorrelationCoefficient coefficient;
	
	public PredictionGraph graph;
	
	public int runs = 10;
	
	public double runPercent = 0.5;
	
	public boolean calculateMatrices = true;
	
	public CorrelationExperiment() {
		setOptionConstraints("template.outputTemplate", new SubclassConstraint(LabelTemplate.class));
		
		setOptionBinding("template", "dataset.template", "graph.template");
		setOptionConstraints("dataset", new CompatibleWith(this, "template"));
		
		setOptionBinding("template.inputTemplate", "inputEncoder.template");
		setOptionConstraints("inputEncoder", new CompatibleWith(this, "template.inputTemplate"));
		setOptionBinding("template.outputTemplate", "outputEncoder.template");
		setOptionConstraints("outputEncoder", new CompatibleWith(this, "template.outputTemplate"));
		
		setOptionBinding("none", "dataset.shuffle");
		
		setOptionChecks("graph.outputClassifier", new ErrorCheck<Block>() {
			@Override public String getError(Block value) {
				if (value.parents.size() > 1)
					return "must have only one parent";
				else
					return null;
			}
		});
		
		setOptionChecks("graph", new ErrorCheck<Block>() {
			@Override public String getError(Block value) {
				if (value.trainingAlgorithm instanceof NoTraining || value.trained == true)
					return null;
				else
					return "cannot be trained inside this experiment";
			}
		});
		
		setOptionChecks("folds", new RangeCheck(RangeType.LOWER, 2.0));
		setOptionChecks("samples", new RangeCheck(RangeType.LOWER, 10.0));
	}

	@Override
	protected CorrelationResult runExperiment(String outputDirectory) {
		CorrelationResult ret = new CorrelationResult();
		
		Block inputEncoder = graph.outputClassifier.getParent(0);

		Dataset complete = dataset.buildDataset();
		HelperEncoder outputEncoder = new HelperEncoder();
		outputEncoder.setOption("template", template.outputTemplate);
		
		for(double count = 0; count < runs; count++) {
			updateStatus(count/runs, "running " + ((int)count+1) + " run of " + runs);
			SampleIterator it = complete.getRandomSubset(runPercent).encodedSampleIterator(inputEncoder, outputEncoder, false);

			RealMatrix input = null, output = null;
			if (calculateMatrices) {
				updateStatus(count/runs, "evaluating input correlation matrix");
				input = coefficient.computeInputCorrelationMatrix(it);
				updateStatus((count+0.33)/runs, "evaluating I-O correlation matrix");
				output = coefficient.computeIOCorrelationMatrix(it);
			}
			updateStatus((count+0.66)/runs, "evaluating synthetic values");
			RealVector v = coefficient.computeSyntheticValues(it);
			
			if (v != null) {
				ret.inputCorrelationMatrices.add(input);
				ret.ioCorrelationMatrices.add(output);
				ret.syntheticValueVectors.add(v);
			} else {
				updateStatus(count/runs, "error during evaluation of synthetic values: retrying run " + ((int)count));
				count--;
			}
		}
		
		return ret;
	}

}
