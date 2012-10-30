package game.plugins.experiments;

import game.core.DataTemplate;
import game.core.DataTemplate.Data;
import game.core.Encoding;
import game.core.blocks.Encoder;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.encoders.IntegerEncoder;
import game.plugins.encoders.OneHotEncoder;
import game.plugins.pipes.Concatenator;

public class HelperEncoder extends Encoder<LabelTemplate> {
	
	public OneHotEncoder oneHot;
	public IntegerEncoder integer;
	public Concatenator concatenator = new Concatenator();
	
	public HelperEncoder() {
		setAsInternalOptions("oneHot", "integer", "concatenator");

		setOptionBinding("template", "oneHot.template", "integer.template");
		
		setOption("oneHot", new OneHotEncoder());
		setOption("integer", new IntegerEncoder());
		
		concatenator.parents.add(oneHot);
		concatenator.parents.add(integer);
	}

	@Override
	public boolean isCompatible(DataTemplate object) {
		return false;
	}

	@Override
	protected Encoding baseEncode(Data input) {
		return null;
	}

	@Override
	public Encoding transform(Data input) {
		return concatenator.transform(input);
	}

	@Override
	protected int getBaseFeatureNumber() {
		return oneHot.getFeatureNumber() + integer.getFeatureNumber();
	}

	@Override
	protected FeatureType getBaseFeatureType(int featureIndex) {
		return FeatureType.NOMINAL;
	}
	
}