package game.plugins.blocks.pipes;

import com.ios.IList;
import com.ios.triggers.MasterSlaveTrigger;

import game.core.ElementTemplate;
import game.core.blocks.Pipe;
import game.plugins.valuetemplates.LabelTemplate;
import game.plugins.valuetemplates.VectorTemplate;

public abstract class VectorToLabel extends Pipe {

	public IList<String> labels;
	
	public VectorToLabel() {
		setContent("labels", new IList<>(String.class));
		outputTemplate.add(new LabelTemplate());
		addTrigger(new MasterSlaveTrigger(this, "labels", "outputTemplate.0.labels"));
	}

	@Override
	public boolean supportsInputTemplate(ElementTemplate inputTemplate) {
		return inputTemplate.isSingletonTemplate(VectorTemplate.class);
	}

}
