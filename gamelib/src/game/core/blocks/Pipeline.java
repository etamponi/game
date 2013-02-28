package game.core.blocks;

import game.core.Block;
import game.core.Data;
import game.core.DatasetTemplate;
import game.core.trainingalgorithms.PipelineTraining;

import com.ios.IList;
import com.ios.Property;
import com.ios.errorchecks.SizeCheck;
import com.ios.listeners.SubPathListener;
import com.ios.triggers.BoundProperties;
import com.ios.triggers.SimpleTrigger;

public class Pipeline extends Block {
	
	public IList<Block> blocks;
	
	public Pipeline() {
		setContent("blocks", new IList<>(Block.class));
		setContent("trainingAlgorithm", new PipelineTraining());
		
		addTrigger(new BoundProperties(this, "blocks.*.datasetTemplate"));
		addTrigger(new SimpleTrigger(new SubPathListener(getProperty("datasetTemplate")), new SubPathListener(getProperty("blocks"))) {
			private Pipeline self = Pipeline.this;
			private boolean listen = true;
			@Override
			public void action(Property changedPath) {
				if (listen) {
					listen = false;
					self.updatePipelineTemplates();
					listen = true;
				}
			}
		});
		addErrorCheck(new SizeCheck("blocks", 1));
	}

	private void updatePipelineTemplates() {
		if (datasetTemplate == null)
			return;
		DatasetTemplate template = datasetTemplate;
		for(Block block: blocks) {
			if (block == null)
				break;
			block.setContent("datasetTemplate", template);
			template = new DatasetTemplate(block.outputTemplate, template.targetTemplate);
		}
		updateOutputTemplate();
	}
	
	@Override
	public Data transform(Data input) {
		for(Block block: blocks)
			input = block.transform(input);
		return input;
	}

	@Override
	public String compatibilityError(DatasetTemplate object) {
		return null;
	}

	@Override
	protected void updateOutputTemplate() {
		setContent("outputTemplate", getContent("blocks.last.outputTemplate"));
	}

}
