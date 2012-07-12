package game.plugins.experiments;

import game.core.experiments.EncoderExperiment;

public class NRunEncoderExperiment extends EncoderExperiment {
	
	public int runs = 10;

	@Override
	protected void runExperiment(String outputDirectory) {
		/*Dataset ds = dataset.buildDataset();
		EncodedSamples samples = ds.encode(inputEncoder, outputEncoder);
		
		int foldSize = samples.size()/runs;
		for(int i = 0; i < runs; i++) {
			EncodedSamples run = new EncodedSamples(samples.subList(0, foldSize));
			encodedDatasets.add(run);
			samples.removeAll(run);
		}*/
	}

	@Override
	public String getTaskDescription() {
		return "generate " + runs + " encoded folds";
	}

}
