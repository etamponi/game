package game.core.blocks;

import game.core.Data;
import game.core.DatasetTemplate;
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
	
	public DatasetTemplate datasetTemplate;
	
	public ElementTemplate outputTemplate;
	
	public CombinationStrategy() {
		addErrorCheck(new CompatibilityCheck("classifiers"));
		addTrigger(new SimpleTrigger<CombinationStrategy>(new SubPathListener(getProperty("classifiers")), new SubPathListener(getProperty("datasetTemplate"))) {
			@Override protected void makeAction(Property changedPath) {
				getRoot().updateOutputTemplate();
			}
		});
	}
	
	public abstract Data combine(List<Data> predictions);
	
	public abstract void updateOutputTemplate();
	
}