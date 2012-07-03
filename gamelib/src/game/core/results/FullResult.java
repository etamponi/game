package game.core.results;

import game.core.Dataset;
import game.core.Experiment;
import game.core.Result;
import game.core.experiments.FullExperiment;

public abstract class FullResult extends Result<Dataset> {

	@Override
	public boolean isCompatible(Experiment exp) {
		return exp instanceof FullExperiment;
	}
	
}
