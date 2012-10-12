package game.plugins.metrics;

import game.core.Metric;
import game.core.Result;
import game.plugins.experiments.CorrelationResult;

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
		List<String> labels = result.experiment.template.outputTemplate.getOption("labels");
		
		String row = "%15s%15.2f%15.2f%15.2f\n";
		
		builder.append(String.format("%15s%15s%15s%15s\n", "", "Mean", "Deviation", "95% conf."));
		for(int i = 0; i < labels.size(); i++) {
			String label = labels.get(i);
			double[] data = getData(result.getPerClassMeasures(), i);
			DescriptiveStatistics stat = new DescriptiveStatistics(data);
			double mean = stat.getMean();
			double stddev = stat.getStandardDeviation();
			builder.append(String.format(row, label, mean, stddev, 0.0));
		}
		
		double[] data = new double[result.getOverallMeasures().size()];
		for(int i = 0; i < data.length; i++) data[i] = result.getOverallMeasures().get(i);
		DescriptiveStatistics stat = new DescriptiveStatistics(data);
		double mean = stat.getMean();
		double stddev = stat.getStandardDeviation();
		builder.append(String.format(row, "Overall", mean, stddev, 0.0));
		
		return builder.toString();
	}

	private double[] getData(List<RealVector> list, int i) {
		double[] ret = new double[list.size()];
		for(int j = 0; j < ret.length; j++)
			ret[j] = list.get(j).getEntry(i);
		return ret;
	}

}
