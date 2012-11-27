package game.plugins.algorithms;

import game.core.Block;
import game.core.Dataset;
import game.core.TrainingAlgorithm;
import game.core.blocks.Classifier;
import game.core.blocks.MetaEnsemble;
import game.plugins.classifiers.MajorityCombiner;
import game.plugins.encoders.OneHotEncoder;
import game.plugins.pipes.LinearTransform;

import com.ios.Property;
import com.ios.errorchecks.RangeCheck;
import com.ios.listeners.ExactPathListener;
import com.ios.triggers.BoundProperties;
import com.ios.triggers.MasterSlaveTrigger;
import com.ios.triggers.SimpleTrigger;

public class TransformationEnsemble extends TrainingAlgorithm<MetaEnsemble> {
	
	public double bootstrapPercent = 0.66;
	
	public int classifiers = 10;
	
	public Classifier baseClassifier;
	
	public LinearTransform baseTransform;
	
	public TransformationEnsemble() {
		addErrorCheck("bootstrapPercent", new RangeCheck(0.001, 1.0));
		
		addTrigger(new SimpleTrigger(new ExactPathListener(new Property(this, "block"))) {
			@Override
			public void action(Property changedPath) {
				Block block = changedPath.getContent();
				block.setContent("outputEncoder", new OneHotEncoder());
				block.setContent("combiner", new MajorityCombiner());
			}
		});
		
		addTrigger(new BoundProperties(this, "baseClassifier.parents", "baseTransform.parents"));
		addTrigger(new MasterSlaveTrigger(this, "block.outputEncoder", "baseClassifier.outputEncoder"));
		addTrigger(new MasterSlaveTrigger(this, "block.template", "baseClassifier.template"));
	}

	@Override
	public boolean isCompatible(Block object) {
		return object instanceof MetaEnsemble;
	}

	@Override
	protected void train(Dataset dataset) {
		updateStatus(0.1, "start growing forest of " + classifiers + " " + baseClassifier.getClass().getSimpleName() + " classifiers with a linear prefiltering.");
		
		for(int i = 0; i < classifiers; i++) {
			updateStatus(0.1 + 0.9*i/classifiers, "growing tree " + (i+1));
			
			LinearTransform transform = baseTransform.copy();
			transform.parents.add(block.getParent(0));
			
			Classifier current = baseClassifier.copy();
			current.parents.add(transform);
			
			Dataset subset = dataset.getRandomSubset(bootstrapPercent); // FIXME Bootstrap sample
			
			executeAnotherTaskAndWait(0.1+0.9*(i+0.5)/classifiers, transform.trainingAlgorithm, subset);
			executeAnotherTaskAndWait(0.1+0.9*(i+1.0)/classifiers, current.trainingAlgorithm, subset);
			block.combiner.parents.add(current);
		}
	}

	@Override
	protected String getManagedPropertyNames() {
		return "combiner outputEncoder";
	}

}
