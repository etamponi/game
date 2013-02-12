package game.plugins.blocks.filters;

import game.plugins.valuetemplates.LabelTemplate;

import org.apache.commons.math3.linear.ArrayRealVector;

public class LabelToInteger extends LabelToVector {

	@Override
	protected void updateLabelMapping() {
		LabelTemplate template = datasetTemplate.sourceTemplate.getSingleton(LabelTemplate.class);
		for(int i = 0; i < template.labels.size(); i++)
			getLabelMapping().put((String)template.labels.get(i), new ArrayRealVector(new double[]{i}));
	}

}
