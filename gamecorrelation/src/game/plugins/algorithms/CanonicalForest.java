package game.plugins.algorithms;

import game.core.Block;
import game.core.Dataset;
import game.core.TrainingAlgorithm;
import game.core.blocks.MetaEnsemble;
import game.plugins.classifiers.DecisionTree;
import game.plugins.classifiers.MajorityCombiner;
import game.plugins.encoders.OneHotEncoder;
import game.plugins.pipes.LinearTransform;
import game.utils.Utils;

import com.ios.Property;
import com.ios.errorchecks.RangeCheck;
import com.ios.listeners.ExactPathListener;
import com.ios.triggers.SimpleTrigger;

public class CanonicalForest extends TrainingAlgorithm<MetaEnsemble> {
	
	public double bootstrapPercent = 0.66;
	
	public int featuresPerNode = 0;
	
	public int trees = 10;
	
	public CanonicalForest() {
		addErrorCheck("bootstrapPercent", new RangeCheck(0.001, 1.0));
		
		addTrigger(new SimpleTrigger(new ExactPathListener(new Property(this, "block"))) {
			@Override
			public void action(Property changedPath) {
				Block block = changedPath.getContent();
				block.setContent("outputEncoder", new OneHotEncoder());
				block.setContent("combiner", new MajorityCombiner());
			}
		});
	}

	@Override
	public boolean isCompatible(Block object) {
		return object instanceof MetaEnsemble;
	}

	@Override
	protected void train(Dataset dataset) {
		int selectedFeatures = featuresPerNode == 0 ? (int)Utils.log2(block.getParent(0).getFeatureNumber()) + 1 : featuresPerNode;
		
		updateStatus(0.1, "start growing forest of " + trees + " trees using random canonical variates.");
		
		for(int i = 0; i < trees; i++) {
			updateStatus(0.1 + 0.9*i/trees, "growing tree " + (i+1));
			
			LinearTransform transform = new LinearTransform();
			transform.setContent("trainingAlgorithm", new CanonicalCorrelation());
			transform.parents.add(block.getParent(0));
						
			DecisionTree tree = new DecisionTree();
			tree.setContent("template", block.template);
			tree.setContent("trainingAlgorithm", new C45Like());
			tree.trainingAlgorithm.setContent("featuresPerNode", selectedFeatures);
			tree.parents.add(transform);
			
			Dataset subset = dataset.getRandomSubset(bootstrapPercent); // FIXME Bootstrap sample
			
			executeAnotherTaskAndWait(0.1+0.9*(i+0.5)/trees, transform.trainingAlgorithm, subset);
			executeAnotherTaskAndWait(0.1+0.9*(i+1.0)/trees, tree.trainingAlgorithm, subset);
			block.combiner.parents.add(tree);
		}
	}

	@Override
	protected String getManagedPropertyNames() {
		return "combiner outputEncoder";
	}

}
