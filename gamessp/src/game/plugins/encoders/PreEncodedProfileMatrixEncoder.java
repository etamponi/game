package game.plugins.encoders;

import game.core.DataTemplate;
import game.plugins.datatemplates.PreEncodedPrimaryStructure;


public class PreEncodedProfileMatrixEncoder extends PerAtomSequenceEncoder {
	
	public PreEncodedProfileMatrixEncoder() {
		setOption("atomEncoder", new VectorEncoder());
		
		setInternalOptions("atomEncoder");
	}

	@Override
	public boolean isCompatible(DataTemplate template) {
		return template instanceof PreEncodedPrimaryStructure;
	}

}
