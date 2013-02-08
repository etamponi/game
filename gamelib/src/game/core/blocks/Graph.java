package game.core.blocks;

import game.core.Block;
import game.core.Data;
import game.core.DatasetTemplate;
import game.core.Instance;
import game.plugins.trainingalgorithms.RandomSplitTraining;

import com.ios.IList;
import com.ios.Property;
import com.ios.listeners.SubPathListener;
import com.ios.triggers.MasterSlaveTrigger;
import com.ios.triggers.SimpleTrigger;

public class Graph extends Block {
	
	private static class Edge {
		
	}
	
	public IList<Block> blocks;
	
	public Block outputBlock;
	
	public Graph() {
		setContent("blocks", new IList<>(Block.class));
		setContent("trainingAlgorithm", new RandomSplitTraining());
		
		addTrigger(new SimpleTrigger(new SubPathListener(getProperty("datasetTemplate"))) {
			private Graph self = Graph.this;
			@Override public void action(Property changedPath) {
				self.updateDatasetTemplateForBlocks();
			}
		});
		addTrigger(new MasterSlaveTrigger(this, "outputBlock.outputTemplate", "outputTemplate"));
		omitFromErrorCheck("blocks");
	}
	
	private void updateDatasetTemplateForBlocks() {
		
	}

	@Override
	public Data transform(Data input) {
		return outputBlock.transform(input);
	}
	
	public Instance classify(Instance inst) {
		Instance ret = new Instance(inst.getSource(), inst.getTarget());
		ret.setPrediction(transform(inst.getSource()));
		return ret;
	}

	@Override
	protected void setup() {
		// nothing to do
	}

	@Override
	public boolean acceptsParents() {
		return false;
	}

}
