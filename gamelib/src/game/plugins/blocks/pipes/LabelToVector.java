package game.plugins.blocks.pipes;

import game.core.Data;
import game.core.ElementTemplate;
import game.core.Element;
import game.core.blocks.Pipe;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.datatemplates.VectorTemplate;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public abstract class LabelToVector extends Pipe {
	
	private final Map<String, RealVector> labelMapping = new HashMap<>();
	
	protected abstract void updateLabelMapping();
	
	public LabelToVector() {
		outputTemplate.add(new VectorTemplate());
	}

	@Override
	protected Data transduce(Data input) {
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
	public boolean supportsInputTemplate(ElementTemplate inputTemplate) {
		return inputTemplate.isSingletonTemplate(LabelTemplate.class);
	}

	@Override
	protected void setup() {
		labelMapping.clear();
		if (getParentTemplate() != null && supportsInputTemplate(getParentTemplate())) {
			updateLabelMapping();
			if (!labelMapping.values().isEmpty())
				outputTemplate.getSingleton().setContent("dimension", labelMapping.values().iterator().next().getDimension());
		}
	}

	protected Map<String, RealVector> getLabelMapping() {
		return labelMapping;
	}

}
