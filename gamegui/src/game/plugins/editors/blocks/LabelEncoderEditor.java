package game.plugins.editors.blocks;

import game.plugins.encoders.LabelEncoder;

public class LabelEncoderEditor extends BlockEditor {

	public LabelEncoderEditor() {
		setSpecificEditor("labelMapping", LabelMappingEditor.class);
	}

	@Override
	public Class getBaseEditableClass() {
		return LabelEncoder.class;
	}
	
}
