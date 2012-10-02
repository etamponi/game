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
package game.core;

import java.io.File;



public abstract class Experiment extends LongTask {
	
	public String name;
	
	public InstanceTemplate template;
	
	public Result startExperiment(String prefixDirectory) {
		return startTask(prefixDirectory);
	}
	
	protected abstract Result runExperiment(String outputDirectory);

	@Override
	protected Object execute(Object... params) {
		String outputDirectory = params[0] + "/" + name;
		File dir = new File(outputDirectory);
		if (!dir.exists())
			dir.mkdirs();
		this.saveConfiguration(outputDirectory+"/"+name+".config.xml");
		Result result = runExperiment(outputDirectory);
		result.experiment = this;
		result.saveConfiguration(outputDirectory + "/result_"+name+".config.xml");
		return result;
	}

}
