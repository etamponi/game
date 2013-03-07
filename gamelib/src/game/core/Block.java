package game.core;


import game.core.trainingalgorithms.NoTraining;

import java.util.ArrayList;

import com.ios.Compatible;
import com.ios.IObject;
import com.ios.Property;
import com.ios.Trigger;
import com.ios.constraints.CompatibilityConstraint;
import com.ios.errorchecks.CompatibilityCheck;
import com.ios.errorchecks.NotNullCheck;
import com.ios.listeners.SubPathListener;
import com.ios.triggers.BoundProperties;
import com.ios.triggers.MasterSlaveTrigger;
import com.ios.triggers.SimpleTrigger;

public abstract class Block extends IObject implements Compatible<DatasetTemplate> {
	
	public boolean trained = false;
	
	@InName
	public TrainingAlgorithm trainingAlgorithm;
	
	public DatasetTemplate datasetTemplate;
	
	public ElementTemplate outputTemplate;
	
	public Block() {
		// To handle training properties
		addTrigger(new MasterSlaveTrigger(this, "", "trainingAlgorithm.block"));
		final Trigger t = new BoundProperties("empty");
		addTrigger(t);
		addTrigger(new SimpleTrigger<Block>(new SubPathListener(getProperty("trainingAlgorithm"))) {
			private Trigger trigger = t;
			@Override
			protected void makeAction(Property changedPath) {
				trigger.getBoundPaths().clear();
				if (getRoot().trainingAlgorithm != null) {
					for(Object path: getRoot().trainingAlgorithm.getTrainingProperties())
						trigger.getBoundPaths().add(path.toString());
				}
				if (trigger.getBoundPaths().isEmpty())
					trigger.getBoundPaths().add("empty");
				
				for(Property linkToThis: new ArrayList<>(getRoot().getParentsLinksToThis())) {
					if (linkToThis.getPath().equals("block") && linkToThis.getRoot() instanceof TrainingAlgorithm)
						if (linkToThis.getRoot() != getRoot().trainingAlgorithm)
							linkToThis.getRoot().detach();
				}
			}
		});
		addConstraint("trainingAlgorithm", new CompatibilityConstraint(getProperty("")));
		
		setContent("trainingAlgorithm", new NoTraining());
		
		addTrigger(new BoundProperties("outputTemplate"));
		addTrigger(new Trigger<Block>() {
			private boolean listen = true;
			@Override public void action(Property changedPath) {
				if (listen) {
					listen = false;
					if (getRoot().datasetTemplate != null && getRoot().compatibilityError(getRoot().datasetTemplate) == null) {
						getRoot().updateOutputTemplate();
					} else {
						getRoot().setContent("outputTemplate", null);
					}
					listen = true;
				}
			}
		});

		addErrorCheck(new CompatibilityCheck("datasetTemplate"));
		addErrorCheck(new NotNullCheck("outputTemplate"));
	}

	public abstract Data transform(Data input);
	
	protected abstract void updateOutputTemplate();

}
