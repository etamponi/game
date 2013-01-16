package game.plugins.blocks.pipes;

import game.plugins.datatemplates.LabelTemplate;

import org.apache.commons.math3.linear.ArrayRealVector;

public class LabelToInteger extends LabelToVector {

	@Override
	protected void updateLabelMapping() {
		LabelTemplate template = getParentTemplate() == null ? null : getParentTemplate().getSingleton(LabelTemplate.class);
		if (template == null)
			return;
		for(int i = 0; i < template.labels.size(); i++)
			getLabelMapping().put((String)template.labels.get(i), new ArrayRealVector(new double[]{i}));
	}

}
