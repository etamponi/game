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
package game.core;

import game.utils.Log;

import java.io.File;

public abstract class Experiment extends LongTask<Result, String> {
	
	public String name;
	
	public InstanceTemplate template;
	
	protected abstract Result runExperiment(String outputDirectory);

	@Override
	public Result execute(String resultsDirectory) {
		updateStatus(0.0, "Starting " + getClass().getSimpleName() + " " + name);
		String outputDirectory = resultsDirectory + "/" + name;
		Log.setCurrentExperiment(name);
		File dir = new File(outputDirectory);
		if (!dir.exists())
			dir.mkdirs();
		this.saveConfiguration(outputDirectory+"/experiment_"+name+".config.xml");
		Result result = runExperiment(outputDirectory);
		result.experiment = this;
		result.saveConfiguration(outputDirectory + "/result_"+name+".config.xml");
		updateStatus(1.0, "Experiment " + getClass().getSimpleName() + " " + name + " finished");
		Log.setCurrentExperiment(null);
		return result;
	}

}
