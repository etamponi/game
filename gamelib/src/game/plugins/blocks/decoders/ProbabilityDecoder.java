package game.plugins.blocks.decoders;

import game.core.Data;
import game.core.DatasetTemplate;
import game.core.Element;
import game.core.blocks.Decoder;
import game.plugins.valuetemplates.LabelTemplate;
import game.plugins.valuetemplates.VectorTemplate;

import java.util.List;

import org.apache.commons.math3.linear.RealVector;

public class ProbabilityDecoder extends Decoder {
	
	@Override
	public Data transform(Data input) {
		List<String> labels = datasetTemplate.targetTemplate.getSingleton(LabelTemplate.class).labels;
		Data output = new Data();
		for (Element element: input) {
			RealVector vec = (RealVector) element.get(0);
			output.add(new Element(labels.get(vec.getMaxIndex())));
		}
		return output;
	}

	@Override
	public String compatibilityError(DatasetTemplate template) {
		if (template == null || !template.isReady())
			return "template is null or is not ready!";
		if (!template.sourceTemplate.isSingletonTemplate(VectorTemplate.class))
			return "sourceTemplate must be a singleton VectorTemplate";
		if (!template.targetTemplate.isSingletonTemplate(LabelTemplate.class))
			return "targetTemplate must be a singleton LabelTemplate";
		int sourceDimension = template.sourceTemplate.getSingleton(VectorTemplate.class).dimension;
		int targetDimension = template.targetTemplate.getSingleton(LabelTemplate.class).labels.size();
		if (sourceDimension == targetDimension)
			return null;
		else
			return "the dimension of the vector must be the same as the label number (found: " + sourceDimension + ", " + targetDimension + ")";
	}

}
