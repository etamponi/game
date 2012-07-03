package game.plugins.results;

import java.util.List;

import game.core.EncodedSample;
import game.core.results.MetricResult;

public class MultipleCorrelation extends MetricResult {

	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void evaluate(List<EncodedSample>... folds) {
		
	}

	@Override
	public String prettyPrint() {
		// TODO Auto-generated method stub
		return null;
	}

}
