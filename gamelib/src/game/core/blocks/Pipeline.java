package game.core.blocks;

import game.core.Block;
import game.core.Data;
import game.core.DatasetTemplate;

import com.ios.IList;
import com.ios.Property;
import com.ios.listeners.SubPathListener;
import com.ios.triggers.SimpleTrigger;

public class Pipeline extends Block {
	
	public IList<Block> blocks;
	
	public Pipeline() {
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
	}

	private void updatePipelineTemplates() {
		DatasetTemplate template = datasetTemplate;
		for(Block block: blocks) {
			block.setContent("datasetTemplate", template);
			template = new DatasetTemplate(block.outputTemplate, template.targetTemplate);
		}
		updateOutputTemplate();
	}
	
	public Data classify(Data input) {
		if (!isClassifier())
			return null;
		return transform(input);
	}

	@Override
	public Data transform(Data input) {
		for(Block block: blocks)
			input = block.transform(input);
		return input;
	}

	@Override
	public boolean isCompatible(DatasetTemplate object) {
		return true;
	}

	@Override
	protected void updateOutputTemplate() {
		if (blocks.isEmpty())
			setContent("outputTemplate", null);
		else
			setContent("outputTemplate", blocks.get(blocks.size()-1).outputTemplate);
	}

	@Override
	public boolean isClassifier() {
		return blocks.isEmpty() ? false : blocks.get(blocks.size()-1).isClassifier();
	}

}
