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
package game.core.experiments;

import game.core.Dataset.EncodedSamples;
import game.core.DatasetBuilder;
import game.core.Experiment;
import game.core.blocks.Encoder;
import game.plugins.constraints.CompatibleWith;

import java.util.List;

public abstract class EncoderExperiment extends Experiment {
	
	public Encoder inputEncoder;
	
	public Encoder outputEncoder;
	
	public DatasetBuilder dataset;
	
	public List<EncodedSamples> encodedDatasets;
	
	public EncoderExperiment() {
		setOptionBinding("template.inputTemplate", "inputEncoder.template");
		setOptionBinding("template.outputTemplate", "outputEncoder.template");
		setOptionBinding("template", "dataset.template");

		setOptionConstraint("inputEncoder", new CompatibleWith(this, "template.inputTemplate"));
		setOptionConstraint("outputEncoder", new CompatibleWith(this, "template.outputTemplate"));
		setOptionConstraint("dataset", new CompatibleWith(this, "template"));
		
		setInternalOptions("encodedDatasets");
	}

}
