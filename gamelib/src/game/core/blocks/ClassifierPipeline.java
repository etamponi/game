package game.core.blocks;

import game.core.Block;
import game.core.Data;
import game.core.DatasetTemplate;
import game.core.trainingalgorithms.PipelineTraining;

import java.util.List;

import com.ios.IList;
import com.ios.Property;
import com.ios.errorchecks.PropertyCheck;
import com.ios.listeners.SubPathListener;
import com.ios.triggers.BoundProperties;
import com.ios.triggers.SimpleTrigger;

public class ClassifierPipeline extends Classifier {
	
	public IList<Block> blocks;
	
	public ClassifierPipeline() {
		setContent("blocks", new IList<>(Block.class));
		setContent("trainingAlgorithm", new PipelineTraining());
		
		addTrigger(new BoundProperties(this, "decoder"));
		addTrigger(new SimpleTrigger(new SubPathListener(getProperty("blocks"))) {
			private ClassifierPipeline self = ClassifierPipeline.this;
			@Override public void action(Property changedPath) {
				Block last = self.blocks.isEmpty() ? null : self.blocks.get(self.blocks.size()-1);
				if (last instanceof Classifier) {
					self.setContent("decoder", last.getContent("decoder"));
				}
			}
		});
		
		addTrigger(new BoundProperties(this, "blocks.*.datasetTemplate"));
		addTrigger(new SimpleTrigger(new SubPathListener(getProperty("datasetTemplate")), new SubPathListener(getProperty("blocks"))) {
			private ClassifierPipeline self = ClassifierPipeline.this;
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
		addErrorCheck(new PropertyCheck<List<Block>>("blocks") {
			@Override
			protected String getError(List<Block> value) {
				if (value.isEmpty())
					return "must contain at least one block";
				if (value.get(value.size()-1) instanceof Classifier)
					return null;
				else
					return "last block must be a Classifier";
			}
		});
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
	}

	@Override
	protected void updateOutputTemplate() {
		setContent("outputTemplate", getContent("blocks.last.outputTemplate"));
	}

	@Override
	public Data classify(Data input) {
		for(Block block: blocks)
			input = block.transform(input);
		return input;
	}

	@Override
	public String classifierCompatibilityError(DatasetTemplate template) {
		return null;
	}

}
