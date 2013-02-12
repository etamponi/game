package game.plugins.blocks.filters;

import game.core.Data;
import game.core.DatasetTemplate;
import game.core.Element;
import game.core.blocks.Filter;
import game.plugins.valuetemplates.LabelTemplate;
import game.plugins.valuetemplates.VectorTemplate;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import com.ios.triggers.BoundProperties;

public abstract class LabelToVector extends Filter {
	
	private final Map<String, RealVector> labelMapping = new HashMap<>();
	
	protected abstract void updateLabelMapping();
	
	public LabelToVector() {
		addTrigger(new BoundProperties(this, "outputTemplate"));
		outputTemplate.add(new VectorTemplate());
	}

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
	public boolean isCompatible(DatasetTemplate template) {
		return template.sourceTemplate.isSingletonTemplate(LabelTemplate.class);
	}

	@Override
	protected void updateOutputTemplate() {
		labelMapping.clear();
		if (datasetTemplate != null && isCompatible(datasetTemplate)) {
			updateLabelMapping();
			if (!labelMapping.values().isEmpty())
				outputTemplate.getSingleton().setContent("dimension", labelMapping.values().iterator().next().getDimension());
		} else {
			outputTemplate.getSingleton().setContent("dimension", 0);
		}
	}

	protected Map<String, RealVector> getLabelMapping() {
		return labelMapping;
	}

}
