package game.plugins.experiments;

import game.core.Dataset;
import game.core.Dataset.EncodedSamples;
import game.core.experiments.EncoderExperiment;

public class NRunMetricsExperiment extends EncoderExperiment {
	
	public int runs = 5;

	@Override
	protected void runExperiment() {
		Dataset ds = dataset.buildDataset();
		EncodedSamples samples = ds.encode(inputEncoder, outputEncoder);
		
		int foldSize = samples.size()/runs;
		for(int i = 0; i < runs; i++) {
			EncodedSamples run = new EncodedSamples(samples.subList(0, foldSize));
			encodedDatasets.add(run);
			samples.removeAll(run);
		}
	}

	@Override
	public String getTaskDescription() {
		return "generate " + runs + " encoded folds";
	}

}
