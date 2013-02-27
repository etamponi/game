package game.core.blocks;

import game.core.Block;
import game.core.Data;
import game.core.DatasetTemplate;
import game.core.trainingalgorithms.BasicEnsembleTraining;

import java.util.ArrayList;
import java.util.List;

import com.ios.IList;
import com.ios.constraints.CompatibleWith;
import com.ios.triggers.MasterSlaveTrigger;

public class Ensemble extends Classifier {
	
	public IList<Classifier> classifiers;
	
	public CombinationStrategy strategy;
	
	public Ensemble() {
		setContent("classifiers", new IList<>(Classifier.class));
		setContent("trainingAlgorithm", new BasicEnsembleTraining());
		
		addTrigger(new MasterSlaveTrigger(this, "classifiers", "strategy.classifiers"));
		addTrigger(new MasterSlaveTrigger(this, "strategy.outputTemplate", "outputTemplate"));
		addConstraint("strategy", new CompatibleWith(getProperty("classifiers")));
		
		addTrigger(new MasterSlaveTrigger(this, "datasetTemplate", "classifiers.*.datasetTemplate", "strategy.datasetTemplate"));
//		addErrorCheck("classifiers", new SizeCheck(1));
	}

	@Override
	public Data classify(Data input) {
		List<Data> predictions = new ArrayList<>();
		for(Block cls: classifiers)
			predictions.add(cls.transform(input));
		return strategy.combine(predictions);
	}

	@Override
	public boolean isClassifierCompatible(DatasetTemplate template) {
		return true;
	}

}
