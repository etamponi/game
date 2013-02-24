package game.plugins.blocks.combinationstrategies;

import game.core.Data;
import game.core.ElementTemplate;
import game.core.blocks.Classifier;
import game.core.blocks.CombinationStrategy;
import game.plugins.blocks.filters.LabelToOneHot;
import game.plugins.valuetemplates.LabelTemplate;
import game.plugins.valuetemplates.VectorTemplate;

import java.util.List;

import org.apache.commons.math3.linear.RealVector;

public class Majority extends CombinationStrategy {

	@Override
	public boolean isCompatible(List<Classifier> list) {
		for (Classifier cls: list) {
			if (cls.decoder == null)
				return false;
		}
		return true;
	}

	@Override
	public Data combine(List<Data> predictions) {
		Data ret = new Data();
		
		LabelToOneHot conv = new LabelToOneHot();
		conv.setContent("datasetTemplate", datasetTemplate.reverseTemplate());
		
		for(int i = 0; i < predictions.size(); i++) {
			Data decoded = classifiers.get(i).decoder.transform(predictions.get(i));
			Data reencoded = conv.transform(decoded);
			
			if (i == 0) {
				ret = reencoded;
				continue;
			}
			for(int j = 0; j < reencoded.length(); j++) {
				RealVector sum = ret.get(j).get();
				RealVector cur = reencoded.get(j).get();
				ret.get(j).set(0, sum.add(cur));
			}
		}
		
		for(int j = 0; j < ret.length(); j++) {
			ret.get(j).get(RealVector.class).mapDivideToSelf(predictions.size());
		}
		
		return ret;
	}

	@Override
	public void updateOutputTemplate() {
		if (datasetTemplate != null && datasetTemplate.isReady())
			setContent("outputTemplate", 
					new ElementTemplate(
							new VectorTemplate(
									datasetTemplate.targetTemplate.getSingleton(LabelTemplate.class).labels.size())));
		else
			setContent("outputTemplate", null);
	}

}
