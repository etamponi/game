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
import game.configuration.errorchecks.SubclassCheck;
import game.core.Block;
import game.core.DataTemplate;
import game.core.Dataset;
import game.core.DatasetBuilder;
import game.core.Encoding;
import game.core.Experiment;
import game.core.NoTraining;
import game.core.DataTemplate.Data;
import game.core.Dataset.SampleIterator;
import game.core.blocks.Encoder;
import game.core.blocks.PredictionGraph;
import game.plugins.constraints.CompatibleWith;
import game.plugins.correlation.CorrelationMeasure;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.encoders.IntegerEncoder;
import game.plugins.encoders.OneHotEncoder;
import game.plugins.pipes.Concatenator;

public class CorrelationExperiment extends Experiment {
	
	public static class HelperEncoder extends Encoder<LabelTemplate> {
		
		public OneHotEncoder oneHot;
		public IntegerEncoder integer;
		public Concatenator concatenator = new Concatenator();
		
		public HelperEncoder() {
			setAsInternalOptions("oneHot", "integer", "concatenator");

			setOptionBinding("template", "oneHot.template", "integer.template");
			
			setOption("oneHot", new OneHotEncoder());
			setOption("integer", new IntegerEncoder());
			
			concatenator.parents.add(oneHot);
			concatenator.parents.add(integer);
		}

		@Override
		public boolean isCompatible(DataTemplate object) {
			return object instanceof LabelTemplate;
		}

		@Override
		protected Encoding baseEncode(Data input) {
			return null;
		}

		@Override
		public Encoding transform(Data input) {
			return concatenator.transform(input);
		}

		@Override
		protected int getBaseFeatureNumber() {
			return oneHot.getFeatureNumber() + integer.getFeatureNumber();
		}
		
	}
	
	public DatasetBuilder dataset;
	
	public CorrelationMeasure measure;
	
	public PredictionGraph graph;
	
	public int runs = 10;
	
	public double runPercent = 0.5;
	
	public int samples = 1000;
	
	public CorrelationExperiment() {
		setOptionChecks("template.outputTemplate", new SubclassCheck(LabelTemplate.class));
		
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
		
		dataset.shuffle = false;
		Block inputEncoder = graph.outputClassifier.getParent(0);

		Dataset complete = dataset.buildDataset();
		HelperEncoder outputEncoder = new HelperEncoder();
		outputEncoder.setOption("template", template.outputTemplate);
		
		for(int count = 0; count < runs; count++) {
			Dataset d = complete.getRandomSubset(runPercent);
			CorrelationMeasure m = measure.cloneConfiguration(measure + "_" + count);
			SampleIterator it = d.encodedSampleIterator(inputEncoder, outputEncoder, false);
			m.evaluateEverything(it, samples);
			ret.measures.add(m);
		}
		
		return ret;
	}

//	@Override
//	public String getTaskDescription() {
//		return "correlation experiment with " + folds + " folds, " + samples + " samples per fold for " + graph;
//	}

}
