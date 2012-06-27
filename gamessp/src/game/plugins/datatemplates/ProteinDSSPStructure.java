package game.plugins.datatemplates;


public class ProteinDSSPStructure extends SequenceTemplate {
	
	private static final String[] types = "H,B,E,G,I,T,S, ".split(",");
	
	public ProteinDSSPStructure() {
		atom = new LabelTemplate();
		for (String type: types)
			atom.setOption("labels.add", type);
		
		setInternalOptions("atom");
	}
}
