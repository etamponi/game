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

import game.core.Result;


public class NRunEncoderExperiment extends EncoderExperiment {
	
	public int runs = 10;

	@Override
	protected Result runExperiment(String outputDirectory) {
		/*Dataset ds = dataset.buildDataset();
		EncodedSamples samples = ds.encode(inputEncoder, outputEncoder);
		
		int foldSize = samples.size()/runs;
		for(int i = 0; i < runs; i++) {
			EncodedSamples run = new EncodedSamples(samples.subList(0, foldSize));
			encodedDatasets.add(run);
			samples.removeAll(run);
		}*/
		return null;
	}

	@Override
	public String getTaskDescription() {
		return "generate " + runs + " encoded folds";
	}

}
