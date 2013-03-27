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

import game.utils.Log;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public abstract class Experiment<R extends Result> extends LongTask<ResultList<R>, String> {
	
	private static Random generator;
	
	@InName
	public String name = String.format("%s", new SimpleDateFormat("yyMMdd-HH.mm").format(new Date()));
	
	public DatasetBuilder datasetBuilder;
	
	public static Random getRandom() {
		if (generator == null) {
			generator = new Random(0);
		}
		return generator;
	}
	
	public long seed = 1;
	
	protected abstract ResultList<R> runExperiment();

	@Override
	public ResultList<R> execute(String resultsDirectory) {
		Log.setCurrentExperiment(name);
		if (seed > 0) {
			generator = new Random(seed);
		}
		updateStatus(0.0, "Starting " + getClass().getSimpleName() + " " + name);
		String outputDirectory = "";
		if (resultsDirectory != null) {
			outputDirectory = resultsDirectory + "/" + name;
			File dir = new File(outputDirectory);
			if (!dir.exists())
				dir.mkdirs();
			this.write(new File(outputDirectory+"/experiment_"+name+".bin"));
		}
		ResultList<R> result = runExperiment();
		if (resultsDirectory != null) {
			result.setContent("experiment", this.copy());
			result.write(new File(outputDirectory + "/results_"+name+".bin"));
		}
		updateStatus(1.0, "Experiment " + getClass().getSimpleName() + " " + name + " finished");
		Log.setCurrentExperiment(null);
		return result;
	}
	
	public Class<? extends Result> getResultType() {
		return (Class)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

}
