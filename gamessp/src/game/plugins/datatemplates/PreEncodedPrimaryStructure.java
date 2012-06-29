package game.plugins.datatemplates;

public class PreEncodedPrimaryStructure extends SequenceTemplate {

	public PreEncodedPrimaryStructure() {
		setOption("atom", new VectorTemplate());
		setOption("atom.featureNumber", 20);
		
		setInternalOptions("atom");
	}
	
}
