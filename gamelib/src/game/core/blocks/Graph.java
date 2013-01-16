package game.core.blocks;

import game.core.Block;
import game.core.Data;
import game.core.ElementTemplate;
import game.core.Instance;
import game.plugins.trainingalgorithms.RandomSplitTraining;

import com.ios.IList;
import com.ios.triggers.MasterSlaveTrigger;

public class Graph extends Block {
	
	public IList<Block> blocks;
	
	public Block outputBlock;
	
	public Graph() {
		setContent("blocks", new IList<>(Block.class));
		setContent("trainingAlgorithm", new RandomSplitTraining());
		addTrigger(new MasterSlaveTrigger(this, "datasetTemplate", "blocks.*.datasetTemplate"));
		addTrigger(new MasterSlaveTrigger(this, "outputBlock.outputTemplate", "outputTemplate"));
		omitFromErrorCheck("blocks");
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
	public boolean supportsInputTemplate(ElementTemplate inputTemplate) {
		return true;
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
