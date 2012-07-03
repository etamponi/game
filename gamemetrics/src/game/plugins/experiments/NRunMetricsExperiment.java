package game.plugins.experiments;

import game.core.Dataset;
import game.core.EncodedSample;
import game.core.experiments.MetricsExperiment;
import game.core.results.MetricResult;
import game.utils.Msg;

import java.util.List;

public class NRunMetricsExperiment extends MetricsExperiment {
	
	public int runs = 5;

	@Override
	protected void runExperiment() {
		Dataset ds = dataset.buildDataset();
		List<EncodedSample> samples = ds.encode(inputEncoder, outputEncoder);
		
		List<EncodedSample>[] folds = split(samples);
		
		for(MetricResult result: results.getList(MetricResult.class)) {
			result.evaluate(folds);
			Msg.data(result.prettyPrint());
		}
	}
	
	private List<EncodedSample>[] split(List<EncodedSample> samples) {
		List<EncodedSample>[] folds = new List[runs];
		
		int foldSize = samples.size()/runs;
		for(int i = 0; i < runs; i++) {
			folds[i] = samples.subList(0, foldSize);
			samples.removeAll(folds[i]);
		}
		
		return folds;
	}

	@Override
	public String getTaskDescription() {
		// TODO Auto-generated method stub
		return null;
	}

}
