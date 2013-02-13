package game.core;


import game.core.trainingalgorithms.NoTraining;

import com.ios.Compatible;
import com.ios.IObject;
import com.ios.Property;
import com.ios.Trigger;
import com.ios.constraints.CompatibleWith;
import com.ios.errorchecks.CompatibilityCheck;
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
	
//	public BlockPosition position;
	
	public Block() {
//		setContent("position", new BlockPosition());
		
		// To handle training properties
		addTrigger(new MasterSlaveTrigger(this, "", "trainingAlgorithm.block"));
		final Trigger t = new BoundProperties(this, "empty");
		addTrigger(t);
		addTrigger(new SimpleTrigger(new SubPathListener(new Property(this, "trainingAlgorithm"))) {
			private Block self = Block.this;
			private Trigger trigger = t;
			@Override
			public void action(Property changedPath) {
				trigger.getBoundProperties().clear();
				if (self.trainingAlgorithm != null) {
					for(Object path: self.trainingAlgorithm.getTrainingProperties())
						trigger.getBoundProperties().add(new Property(self, path.toString()));
				}
				if (trigger.getBoundProperties().isEmpty())
					trigger.getBoundProperties().add(new Property(self, "empty"));
			}
		});
		addConstraint("trainingAlgorithm", new CompatibleWith(getProperty("")));
		setContent("trainingAlgorithm", new NoTraining());
		
		addTrigger(new BoundProperties(this, "outputTemplate"));
		addTrigger(new SimpleTrigger(new SubPathListener(getProperty(""))) {
			private Block self = Block.this;
			private boolean listen = true;
			@Override public void action(Property changedPath) {
				if (listen) {
					listen = false;
					if (self.datasetTemplate != null && self.isCompatible(self.datasetTemplate)) {
						self.updateOutputTemplate();
					} else {
						self.setContent("outputTemplate", null);
					}
					listen = true;
				}
			}
		});

		addErrorCheck("datasetTemplate", new CompatibilityCheck(this));
	}

	public abstract Data transform(Data input);
	
	protected abstract void updateOutputTemplate();

}
