package game.core.blocks;

import game.core.Block;
import game.core.Data;
import game.core.DatasetTemplate;
import game.core.ElementTemplate;
import game.plugins.valuetemplates.LabelTemplate;
import game.plugins.valuetemplates.VectorTemplate;

import com.ios.Property;
import com.ios.constraints.CompatibilityConstraint;
import com.ios.errorchecks.PropertyCheck;
import com.ios.listeners.SubPathListener;
import com.ios.triggers.BoundProperties;
import com.ios.triggers.MasterSlaveTrigger;
import com.ios.triggers.SimpleTrigger;


public abstract class Classifier extends Block {
	
	public Decoder decoder;
	
	public DatasetTemplate decoderTemplate;
	
	public Classifier() {
		omitFromErrorCheck("decoder");
		
		addTrigger(new BoundProperties("decoderTemplate"));
		addTrigger(new SimpleTrigger<Classifier>(
				new SubPathListener(getProperty("datasetTemplate")),
				new SubPathListener(getProperty("outputTemplate"))) {
			@Override protected void makeAction(Property changedPath) {
				if (getRoot().datasetTemplate != null && getRoot().outputTemplate != null)
					getRoot().setContent("decoderTemplate", new DatasetTemplate(getRoot().outputTemplate, getRoot().datasetTemplate.targetTemplate));
				else
					getRoot().setContent("decoderTemplate", null);
			}
		});
		addTrigger(new MasterSlaveTrigger(this, "decoderTemplate", "decoder.datasetTemplate"));
		addConstraint("decoder", new CompatibilityConstraint(getProperty("decoderTemplate")));
		
		addErrorCheck(new PropertyCheck<ElementTemplate>("outputTemplate") {
			@Override
			protected String getError(ElementTemplate value) {
				if (!value.isSingletonTemplate(VectorTemplate.class))
					return "Classifier must output a single Vector";
				else
					return null;
			}
		});
	}
	
	public abstract Data classify(Data input);
	
	public abstract String classifierCompatibilityError(DatasetTemplate template);
	
	@Override
	public Data transform(Data input) {
		return classify(input);
	}
	
	@Override
	public String compatibilityError(DatasetTemplate template) {
		if (template == null || !template.isReady())
			return "datasetTemplate is null or is not ready";
		if (!template.targetTemplate.isSingletonTemplate(LabelTemplate.class))
			return "targetTemplate must be a singleton LabelTemplate";
		return classifierCompatibilityError(template);
	}

	@Override
	protected void updateOutputTemplate() {
		// For Classifier, the outputTemplate is generally managed by the training algorithm
	}

}
