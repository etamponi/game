package game.plugins.blocks.filters;

import game.plugins.valuetemplates.LabelTemplate;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class LabelToOneHot extends LabelToVector {

	@Override
	protected void updateLabelMapping() {
		LabelTemplate template = datasetTemplate.sourceTemplate.getSingleton(LabelTemplate.class);
		for(int i = 0; i < template.labels.size(); i++) {
			RealVector mapping = new ArrayRealVector(template.labels.size());
			mapping.setEntry(i, 1);
			getLabelMapping().put((String)template.labels.get(i), mapping); 
		}
	}

}
