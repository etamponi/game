package game.plugins.blocks.filters;

import game.core.Data;
import game.core.DatasetTemplate;
import game.core.Element;
import game.core.ElementTemplate;
import game.core.blocks.Filter;
import game.plugins.valuetemplates.LabelTemplate;
import game.plugins.valuetemplates.VectorTemplate;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public abstract class LabelToVector extends Filter {
	
	private final Map<String, RealVector> labelMapping = new HashMap<>();
	
	protected abstract void updateLabelMapping();

	@Override
	public Data transform(Data input) {
		Data ret = new Data();
		for (int j = 0; j < input.length(); j++) {
			String label = (String) input.get(j).get(0);
			RealVector enc;
			if (labelMapping.containsKey(label))
				enc = labelMapping.get(label).copy();
			else
				enc = new ArrayRealVector(outputTemplate.getSingleton(VectorTemplate.class).dimension);
			ret.add(new Element(enc));
		}
		return ret;
	}

	@Override
	public String compatibilityError(DatasetTemplate template) {
		if (template.sourceTemplate.isSingletonTemplate(LabelTemplate.class))
			return null;
		else
			return "sourceTemplate must be a singleton LabelTemplate";
	}

	@Override
	protected void updateOutputTemplate() {
		labelMapping.clear();
		updateLabelMapping();
		if (!labelMapping.values().isEmpty()) {
			int dimension = labelMapping.values().iterator().next().getDimension();
			setContent("outputTemplate", new ElementTemplate(new VectorTemplate(dimension)));
		}
	}

	protected Map<String, RealVector> getLabelMapping() {
		return labelMapping;
	}

}
