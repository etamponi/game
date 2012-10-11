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
package game.plugins.experiments;

import game.configuration.errorchecks.RangeCheck;
import game.configuration.errorchecks.RangeCheck.RangeType;
import game.configuration.errorchecks.SubclassCheck;
import game.core.Dataset;
import game.core.DatasetBuilder;
import game.core.Experiment;
import game.core.Dataset.SampleIterator;
import game.core.blocks.Encoder;
import game.plugins.constraints.CompatibleWith;
import game.plugins.correlation.CorrelationMeasure;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.encoders.IntegerEncoder;
import game.plugins.encoders.OneHotEncoder;

import java.util.List;

public class CorrelationExperiment extends Experiment {
	
	public DatasetBuilder dataset;
	
	public CorrelationMeasure measure;
	
	public Encoder inputEncoder;
	
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
		
		setOptionChecks("folds", new RangeCheck(RangeType.LOWER, 2.0));
		setOptionChecks("samples", new RangeCheck(RangeType.LOWER, 100.0));
	}

	@Override
	protected CorrelationResult runExperiment(String outputDirectory) {
		CorrelationResult ret = new CorrelationResult();
		
		Dataset complete = dataset.buildDataset();
		List<Dataset> split = complete.getFolds(folds);
		for(Dataset d: split) {
			SampleIterator it;
			it = d.encodedSampleIterator(inputEncoder, new OneHotEncoder(), false);
			ret.getPerClassMeasures().add(measure.evaluate(it, samples));
			it = d.encodedSampleIterator(inputEncoder, new IntegerEncoder(), false);
			ret.getOverallMeasures().add(measure.evaluate(it, samples).getEntry(0));
		}
		
		return ret;
	}

	@Override
	public String getTaskDescription() {
		// TODO Auto-generated method stub
		return null;
	}

}
