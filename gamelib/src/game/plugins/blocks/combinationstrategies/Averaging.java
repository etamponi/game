package game.plugins.blocks.combinationstrategies;

import game.core.Data;
import game.core.blocks.Classifier;
import game.core.blocks.CombinationStrategy;

import java.util.List;

import org.apache.commons.math3.linear.RealVector;

public class Averaging extends CombinationStrategy {

	@Override
	public String compatibilityError(List<Classifier> list) {
		if (list.isEmpty())
			return "must contain at least one Classifier";
		Classifier first = list.get(0);
		if (first == null || first.outputTemplate == null)
			return "classifiers must be valid and with the same outputTemplate";
		for(int i = 1; i < list.size(); i++) {
			Classifier curr = list.get(i);
			if (curr == null || curr.outputTemplate == null)
				return "classifiers must be valid and with the same outputTemplate";
			if (!curr.outputTemplate.equals(first.outputTemplate))
				return "classifiers must be valid and with the same outputTemplate";
		}
		return null;
	}

	@Override
	public Data combine(List<Data> predictions) {
		Data ret = new Data(predictions.get(0));
		
		for(int i = 1; i < predictions.size(); i++) {
			Data curr = predictions.get(i);
			for(int j = 0; j < curr.length(); j++) {
				RealVector tot = ret.get(j).get();
				RealVector cur = curr.get(j).get();
				ret.get(j).set(0, tot.add(cur));
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
