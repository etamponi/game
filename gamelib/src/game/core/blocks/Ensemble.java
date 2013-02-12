package game.core.blocks;

import game.core.Block;
import game.core.Data;
import game.core.DatasetTemplate;

import java.util.ArrayList;
import java.util.List;

import com.ios.Compatible;
import com.ios.ErrorCheck;
import com.ios.IList;
import com.ios.IObject;
import com.ios.Property;
import com.ios.constraints.CompatibleWith;
import com.ios.errorchecks.CompatibilityCheck;
import com.ios.errorchecks.SizeCheck;
import com.ios.listeners.ExactPathListener;
import com.ios.triggers.MasterSlaveTrigger;
import com.ios.triggers.SimpleTrigger;

public class Ensemble extends Classifier {
	
	public static abstract class CombinationStrategy extends IObject implements Compatible<Ensemble> {
		
		public Ensemble ensemble;
		
		public CombinationStrategy() {
			addTrigger(new SimpleTrigger(new ExactPathListener(getProperty("ensemble.strategy"))) {
				private CombinationStrategy self = CombinationStrategy.this;
				@Override public void action(Property changedPath) {
					if (self != self.ensemble.strategy)
						self.setContent("ensemble", null);
				}
			});
			addErrorCheck("ensemble", new CompatibilityCheck(this));
		}
		
		public abstract Data combine(List<Data> predictions);
		
	}
	
	public IList<Block> blocks;
	
	public CombinationStrategy strategy;
	
	public Ensemble() {
		addTrigger(new MasterSlaveTrigger(this, "", "strategy.ensemble"));
		addConstraint("strategy", new CompatibleWith(getProperty("")));
		
		setContent("blocks", new IList<>(Block.class));
		addTrigger(new MasterSlaveTrigger(this, "datasetTemplate", "blocks.*.datasetTemplate"));
		addErrorCheck("blocks", new ErrorCheck<List<Block>>() {
			@Override
			public String getError(List<Block> value) {
				for(Block block: value)
					if (!block.isClassifier())
						return "must be all classifiers";
				return null;
			}
		});
		addErrorCheck("blocks", new SizeCheck(1));
	}

	@Override
	public Data classify(Data input) {
		List<Data> predictions = new ArrayList<>();
		for(Block block: blocks)
			predictions.add(block.transform(input));
		return strategy.combine(predictions);
	}

	@Override
	public boolean isClassifierCompatible(DatasetTemplate template) {
		return true;
	}

	@Override
	protected void updateOutputTemplate() {
		// nothing to do
	}

}
