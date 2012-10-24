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
import game.plugins.experiments.CorrelationResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.linear.RealVector;
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
		List<String> labels = new ArrayList<>(result.experiment.template.outputTemplate.getOption("labels",List.class));
		labels.add("Overall");
		
		String row = "%15s%15.3f%15.3f%15.3f%15.3f%15.3f\n";
		
		builder.append(String.format("%15s%15s%15s%15s%15s%15s\n", "", "Median", "Mean", "Std dev.", "Min", "Max"));
		for(int i = 0; i < labels.size(); i++) {
			String label = labels.get(i);
			double[] data = getData(i);
			Arrays.sort(data);
			DescriptiveStatistics stat = new DescriptiveStatistics(data);
			builder.append(String.format(row, label,
					data[data.length/2],
					stat.getMean(),
					stat.getStandardDeviation(),
					stat.getMin(),
					stat.getMax()));
		}

		return builder.toString();
	}

	private double[] getData(int i) {
		double[] ret = new double[result.syntheticValueVectors.size()];
		for(int j = 0; j < ret.length; j++)
			ret[j] = result.syntheticValueVectors.get(j, RealVector.class).getEntry(i);
		return ret;
	}

}
