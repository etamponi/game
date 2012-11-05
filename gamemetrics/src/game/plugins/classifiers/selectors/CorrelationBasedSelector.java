package game.plugins.classifiers.selectors;

import game.core.Block;
import game.core.Dataset;
import game.plugins.classifiers.FeatureSelector;
import game.plugins.correlation.CorrelationCoefficient;

import java.util.List;

public class CorrelationBasedSelector extends FeatureSelector {
	
	public int runs = 10;
	
	public double datasetPercent = 0.5;
	
	public CorrelationCoefficient coefficient;

	@Override
	public void prepare(Dataset dataset, Block inputEncoder) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Integer> select(int n, int[] timesChoosen) {
		// TODO Auto-generated method stub
		return null;
	}

}
