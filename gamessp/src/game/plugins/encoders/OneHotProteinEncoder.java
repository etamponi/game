package game.plugins.encoders;

import game.core.DataTemplate;
import game.plugins.datatemplates.ProteinStructureTemplate;

public class OneHotProteinEncoder extends PerAtomSequenceEncoder {
	
	public OneHotProteinEncoder() {
		setOption("atomEncoder", new OneHotEncoder());
		
		setInternalOptions("atomEncoder");
	}

	@Override
	public boolean isCompatible(DataTemplate template) {
		return template instanceof ProteinStructureTemplate;
	}

}
