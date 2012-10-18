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
import game.core.Dataset;
import game.core.Dataset.SampleIterator;
import game.core.DatasetBuilder;
import game.core.Experiment;
import game.core.NoTraining;
import game.core.blocks.Encoder;
import game.core.blocks.PredictionGraph;
import game.plugins.constraints.CompatibleWith;
import game.plugins.correlation.CorrelationMeasure;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.encoders.IntegerEncoder;
import game.plugins.encoders.OneHotEncoder;

import java.util.List;

public class CorrelationExperiment extends Experiment {
	
	public DatasetBuilder dataset;
	
	public CorrelationMeasure measure;
	
	public PredictionGraph graph;
	
	public int folds = 10;
	
	public int samples = 10000;
	
	public CorrelationExperiment() {
		setOptionChecks("template.outputTemplate", new SubclassCheck(LabelTemplate.class));
		
		setOptionBinding("template", "dataset.template");
		setOptionConstraints("dataset", new CompatibleWith(this, "template"));
		
		setOptionBinding("template.inputTemplate", "inputEncoder.template");
		setOptionConstraints("inputEncoder", new CompatibleWith(this, "template.inputTemplate"));
		setOptionBinding("template.outputTemplate", "outputEncoder.template");
		setOptionConstraints("outputEncoder", new CompatibleWith(this, "template.outputTemplate"));
		
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
		
		Dataset complete = dataset.buildDataset();
		List<Dataset> split = complete.getFolds(folds);
		
		Block inputEncoder = graph.outputClassifier.getParent(0);
		
		for(Dataset d: split) {
			SampleIterator it;
			Encoder encoder = new OneHotEncoder();
			encoder.setOption("template", template.outputTemplate);
			it = d.encodedSampleIterator(inputEncoder, encoder, false);
			ret.getPerClassMeasures().add(measure.evaluate(it, samples));
			encoder = new IntegerEncoder();
			encoder.setOption("template", template.outputTemplate);
			it = d.encodedSampleIterator(inputEncoder, encoder, false);
			ret.getOverallMeasures().add(measure.evaluate(it, samples).getEntry(0));
		}
		
		return ret;
	}

	@Override
	public String getTaskDescription() {
		return "correlation experiment with " + folds + " folds, " + samples + " samples per fold for " + graph;
	}

}
