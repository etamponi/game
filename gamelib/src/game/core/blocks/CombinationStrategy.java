package game.core.blocks;

import game.core.Data;
import game.core.ElementTemplate;

import java.util.List;

import com.ios.Compatible;
import com.ios.IObject;
import com.ios.Property;
import com.ios.errorchecks.CompatibilityCheck;
import com.ios.listeners.SubPathListener;
import com.ios.triggers.SimpleTrigger;

public abstract class CombinationStrategy extends IObject implements Compatible<List<Classifier>> {
	
	public List<Classifier> classifiers;
	
	public ElementTemplate outputTemplate;
	
	public CombinationStrategy() {
		addErrorCheck("classifiers", new CompatibilityCheck(this));
		addTrigger(new SimpleTrigger(new SubPathListener(getProperty("classifiers"))) {
			private CombinationStrategy self = CombinationStrategy.this;
			@Override public void action(Property changedPath) {
				self.updateOutputTemplate();
			}
		});
	}
	
	public abstract Data combine(List<Data> predictions);
	
	public abstract void updateOutputTemplate();
	
}