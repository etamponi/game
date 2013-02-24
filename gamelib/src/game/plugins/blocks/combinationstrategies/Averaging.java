package game.plugins.blocks.combinationstrategies;

import game.core.Data;
import game.core.blocks.Classifier;
import game.core.blocks.CombinationStrategy;

import java.util.List;

import org.apache.commons.math3.linear.RealVector;

public class Averaging extends CombinationStrategy {

	@Override
	public boolean isCompatible(List<Classifier> list) {
		if (list.isEmpty())
			return false;
		Classifier first = list.get(0);
		if (first == null || first.outputTemplate == null)
			return false;
		for(int i = 1; i < list.size(); i++) {
			Classifier curr = list.get(i);
			if (curr == null || curr.outputTemplate == null)
				return false;
			if (!curr.outputTemplate.equals(first.outputTemplate))
				return false;
		}
		return true;
	}

	@Override
	public Data combine(List<Data> predictions) {
		Data ret = new Data(predictions.get(0));
		
		for(int i = 1; i < predictions.size(); i++) {
			Data curr = predictions.get(i);
			for(int j = 0; j < curr.length(); j++) {
				RealVector tot = ret.get(j).get();
				RealVector cur = curr.get(j).get();
				tot = tot.add(cur);
			}
		}
		
		for(int j = 0; j < ret.length(); j++) {
			ret.get(j).get(RealVector.class).mapDivideToSelf(predictions.size());
		}
		
		return ret;
	}

	@Override
	public void updateOutputTemplate() {
		if (classifiers != null && !classifiers.isEmpty())
			setContent("outputTemplate", classifiers.get(0).outputTemplate);
		else
			setContent("outputTemplate", null);
	}

}
