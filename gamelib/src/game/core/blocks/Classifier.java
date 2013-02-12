package game.core.blocks;

import game.core.Block;
import game.core.Data;
import game.core.DatasetTemplate;
import game.core.ElementTemplate;
import game.plugins.valuetemplates.LabelTemplate;
import game.plugins.valuetemplates.VectorTemplate;

import com.ios.Property;
import com.ios.constraints.CompatibleWith;
import com.ios.listeners.SubPathListener;
import com.ios.triggers.BoundProperties;
import com.ios.triggers.MasterSlaveTrigger;
import com.ios.triggers.SimpleTrigger;


public abstract class Classifier extends Block {
	
	public Decoder decoder;
	
	public DatasetTemplate decoderTemplate;
	
	public Classifier() {
		setContent("outputTemplate", new ElementTemplate(new VectorTemplate(0)));
		
		omitFromErrorCheck("decoder");
		
		addTrigger(new BoundProperties(this, "decoderTemplate"));
		addTrigger(new SimpleTrigger(
				new SubPathListener(getProperty("datasetTemplate")),
				new SubPathListener(getProperty("outputTemplate"))) {
			private Classifier self = Classifier.this;
			@Override public void action(Property changedPath) {
				if (self.datasetTemplate != null && self.outputTemplate != null)
					self.setContent("decoderTemplate", new DatasetTemplate(self.outputTemplate, self.datasetTemplate.targetTemplate));
				else
					self.setContent("decoderTemplate", null);
			}
		});
		addTrigger(new MasterSlaveTrigger(this, "decoderTemplate", "decoder.datasetTemplate"));
		addConstraint("decoder", new CompatibleWith(getProperty("decoderTemplate")));
	}
	
	public abstract Data classify(Data input);
	
	public abstract boolean isClassifierCompatible(DatasetTemplate template);
	
	@Override
	public Data transform(Data input) {
		return classify(input);
	}
	
	@Override
	public boolean isCompatible(DatasetTemplate template) {
		return template.targetTemplate.isSingletonTemplate(LabelTemplate.class) && isClassifierCompatible(template);
	}

	@Override
	public boolean isClassifier() {
		return true;
	}

}
