package game.plugins.blocks.filters;

import game.core.Data;
import game.core.DatasetTemplate;
import game.core.blocks.Filter;
import game.plugins.valuetemplates.LabelTemplate;
import game.plugins.valuetemplates.VectorTemplate;

import com.ios.IList;
import com.ios.triggers.BoundProperties;
import com.ios.triggers.MasterSlaveTrigger;

public class VectorToLabels extends Filter {
	
	public VectorToLabels() {
		setContent("labels", new IList<>(String.class));
		
		outputTemplate.add(new LabelTemplate());
		addTrigger(new BoundProperties(this, "outputTemplate"));
		
		addTrigger(new MasterSlaveTrigger(this, "labels", "outputTemplate.0.labels"));
	}

	@Override
	public boolean isCompatible(DatasetTemplate template) {
		return template.sourceTemplate.isSingletonTemplate(VectorTemplate.class);
	}

	@Override
	public Data transform(Data input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void updateOutputTemplate() {
		// TODO Auto-generated method stub
		
	}

}
