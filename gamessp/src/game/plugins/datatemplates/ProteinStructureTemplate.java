package game.plugins.datatemplates;

public abstract class ProteinStructureTemplate extends SequenceTemplate {
	
	public ProteinStructureTemplate() {
		setOption("atom", new LabelTemplate());
		
		setInternalOptions("atom");
	}

}
