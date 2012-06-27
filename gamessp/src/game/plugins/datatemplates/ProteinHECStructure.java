package game.plugins.datatemplates;


public class ProteinHECStructure extends SequenceTemplate {
	
	private static final String[] types = "H E C".split(" ");
	
	public ProteinHECStructure() {
		atom = new LabelTemplate();
		for (String type: types)
			atom.setOption("labels.add", type);
		
		setInternalOptions("atom");
	}
	
}
