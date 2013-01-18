package game.core;


import game.core.trainingalgorithms.NoTraining;

import java.util.List;

import com.ios.ErrorCheck;
import com.ios.IList;
import com.ios.IObject;
import com.ios.Property;
import com.ios.Trigger;
import com.ios.constraints.CompatibleWith;
import com.ios.listeners.SubPathListener;
import com.ios.triggers.BoundProperties;
import com.ios.triggers.MasterSlaveTrigger;
import com.ios.triggers.SimpleTrigger;

public abstract class Block extends IObject {
	
	public boolean trained = false;
	
	public IList<Block> parents;
	
	public TrainingAlgorithm trainingAlgorithm;
	
	public DatasetTemplate datasetTemplate;
	
	public ElementTemplate outputTemplate;
	
	public BlockPosition position;
	
	public Block() {
		setContent("parents", new IList<>(Block.class));
		setContent("position", new BlockPosition());
		setContent("outputTemplate", new ElementTemplate());
		omitFromErrorCheck("parents", "position");
		omitFromPropagation("*.parents.*.parents.*", "*.parents.*.trainingAlgorithm.block");
		
//		addTrigger(new BoundProperties(this, "outputTemplate"));
		addTrigger(new MasterSlaveTrigger(this, "", "trainingAlgorithm.block"));
		
		setContent("trainingAlgorithm", new NoTraining());
		
		final Trigger t = new BoundProperties(this, "empty");
		addTrigger(t);
		addTrigger(new SimpleTrigger(new SubPathListener(new Property(this, "trainingAlgorithm"))) {
			private Block block = Block.this;
			private Trigger trigger = t;
			@Override
			public void action(Property changedPath) {
				trigger.getBoundProperties().clear();
				
				if (block.trainingAlgorithm != null) {
					for(Object path: block.trainingAlgorithm.getTrainingProperties())
						trigger.getBoundProperties().add(new Property(block, path.toString()));
				}
				if (trigger.getBoundProperties().isEmpty())
					trigger.getBoundProperties().add(new Property(block, "empty"));
				
				
			}
		});
		
		addTrigger(new SimpleTrigger(new SubPathListener(new Property(this, "parents"))) {
			private Block block = Block.this;
			@Override
			public void action(Property changedPath) {
				block.setup();
			}
		});

		addConstraint("trainingAlgorithm", new CompatibleWith(new Property(this, "")));

		addErrorCheck("parents", new ErrorCheck<List>() {
			private Block block = Block.this;
			@Override
			public String getError(List parents) {
				for (Block parent: (List<Block>)parents) {
					if (!block.supportsInputTemplate(parent.outputTemplate))
						return "cannot handle " + parent.outputTemplate;
				}
				return null;
			}
		});
	}

	public abstract Data transform(Data input);
	
	public abstract boolean supportsInputTemplate(ElementTemplate inputTemplate);
	
	public abstract boolean acceptsParents();
	
	protected abstract void setup();

}
