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
	
	public InstanceTemplate template;
	
	protected abstract Result runExperiment(String outputDirectory);

	@Override
	public Result execute(String resultsDirectory) {
		Log.setCurrentExperiment(name);
		updateStatus(0.0, "Starting " + getClass().getSimpleName() + " " + name);
		String outputDirectory = resultsDirectory + "/" + name;
		File dir = new File(outputDirectory);
		if (!dir.exists())
			dir.mkdirs();
		this.write(new File(outputDirectory+"/experiment_"+name+".bin"));
		Result result = runExperiment(outputDirectory);
		result.experiment = this;
		result.write(new File(outputDirectory + "/result_"+name+".bin"));
		updateStatus(1.0, "Experiment " + getClass().getSimpleName() + " " + name + " finished");
		Log.setCurrentExperiment(null);
		return result;
	}

}
