package game.core.blocks;

import game.core.Block;
import game.core.Data;
import game.core.DatasetTemplate;
import game.core.trainingalgorithms.PipelineTraining;

import java.util.List;

import com.ios.ErrorCheck;
import com.ios.IList;
import com.ios.Property;
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
		/*
		addTrigger(new MasterSlaveTrigger<List<Block>>(this, "blocks", "decoder") {
			@Override
			protected void updateSlave(Property slave, List<Block> masterContent) {
				Block last = masterContent.isEmpty() ? null : masterContent.get(masterContent.size()-1);
				if (last instanceof Classifier) {
					slave.setContent(last.getContent("decoder"));
				}
			}
		});
		*/
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
		addErrorCheck("blocks", new ErrorCheck<List<Block>>() {
			@Override
			public String getError(List<Block> value) {
				if (value.isEmpty())
					return null;
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
		/*
		if (blocks.isEmpty() || blocks.getContent("last") == null)
			setContent("outputTemplate", null);
		else
			setContent("outputTemplate", blocks.get(blocks.size()-1).outputTemplate);
		*/
	}

	@Override
	public Data classify(Data input) {
		for(Block block: blocks)
			input = block.transform(input);
		return input;
	}

	@Override
	public boolean isClassifierCompatible(DatasetTemplate template) {
		return true;
	}

}
