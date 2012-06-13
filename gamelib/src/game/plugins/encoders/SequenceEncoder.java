package game.plugins.encoders;

import game.configuration.errorchecks.PositivenessCheck;
import game.core.Encoding;
import game.core.blocks.Encoder;
import game.plugins.constraints.CompatibleEncoderConstraint;
import game.plugins.datatemplates.SequenceTemplate;

import java.util.List;

public class SequenceEncoder extends Encoder<SequenceTemplate> {
	
	public Encoder atomEncoder;
	
	public int windowSize = 1;
	
	public SequenceEncoder() {
		addOptionBinding("template.atom", "atomEncoder.template");
		
		setOptionConstraint("atomEncoder", new CompatibleEncoderConstraint(this, "template.atom"));
		
		addOptionChecks("windowSize", new PositivenessCheck(false));
	}
	
	@Override
	public Class getBaseTemplateClass() {
		return SequenceTemplate.class;
	}

	@Override
	protected Encoding transform(Object inputData) {
		Encoding ret = new Encoding();
		
		List input = (List)inputData;
		for(Object atom: input)
			ret.addAll(atomEncoder.startTransform(atom));
		
		return ret;
	}

}
