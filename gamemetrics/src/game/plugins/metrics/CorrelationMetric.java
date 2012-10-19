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
package game.plugins.metrics;

import game.core.Metric;
import game.core.Result;
import game.plugins.correlation.CorrelationMeasure;
import game.plugins.experiments.CorrelationResult;

import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class CorrelationMetric extends Metric<CorrelationResult> {
	
	private CorrelationResult result;

	@Override
	public boolean isCompatible(Result object) {
		return object instanceof CorrelationResult;
	}

	@Override
	public boolean isReady() {
		return result != null;
	}

	@Override
	public void evaluate(CorrelationResult result) {
		this.result = result;
	}

	@Override
	public String prettyPrint() {
		StringBuilder builder = new StringBuilder();
		List<String> labels = result.experiment.template.outputTemplate.getOption("labels");
		labels.add("Overall");
		
		String row = "%15s%15.2f%15.2f%15.2f\n";
		
		builder.append(String.format("%15s%15s%15s%15s\n", "", "Mean", "Deviation", "95% conf."));
		for(int i = 0; i < labels.size(); i++) {
			String label = labels.get(i);
			double[] data = getData(i);
			DescriptiveStatistics stat = new DescriptiveStatistics(data);
			double mean = stat.getMean();
			double stddev = stat.getStandardDeviation();
			builder.append(String.format(row, label, mean, stddev, 0.0));
		}

		return builder.toString();
	}

	private double[] getData(int i) {
		double[] ret = new double[result.measures.size()];
		for(int j = 0; j < ret.length; j++)
			ret[j] = result.measures.get(j, CorrelationMeasure.class).syntheticValues.getEntry(i);
		return ret;
	}

}
