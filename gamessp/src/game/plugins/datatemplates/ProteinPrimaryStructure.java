package game.plugins.datatemplates;


public class ProteinPrimaryStructure extends SequenceTemplate {

	private static final String[] aminoacids = "A R N D C E Q G H I L K M F P S T W Y V".split(" ");
	
	public ProteinPrimaryStructure() {
		atom = new LabelTemplate();
		for (String aminoacid: aminoacids)
			atom.setOption("labels.add", aminoacid);
		
		setInternalOptions("atom");
	}
	
}
