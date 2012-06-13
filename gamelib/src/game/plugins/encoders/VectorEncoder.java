package game.plugins.encoders;

import game.core.Encoding;
import game.core.blocks.Encoder;
import game.plugins.datatemplates.VectorTemplate;

public class VectorEncoder extends Encoder<VectorTemplate> {

	@Override
	public Class getBaseTemplateClass() {
		return VectorTemplate.class;
	}

	@Override
	protected Encoding transform(Object inputData) {
		Encoding ret = new Encoding();
		ret.add((double[])inputData);
		return ret;
	}

}
