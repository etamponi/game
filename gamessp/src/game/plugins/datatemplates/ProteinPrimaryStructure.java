package game.plugins.datatemplates;

import java.util.LinkedList;

public class ProteinPrimaryStructure extends SequenceTemplate {

	private static final LinkedList<String> aminoacids = new LinkedList<>();
	
	static {
		for (String aminoacid: "A R N D C E Q G H I L K M F P S T W Y V".split(" "))
			aminoacids.add(aminoacid);
	}
	
	public ProteinPrimaryStructure() {
		atom = new LabelTemplate();
		for (String aminoacid: aminoacids)
			atom.setOption("labels.add", aminoacid);
		
		setInternalOptions("atom");
	}
	
}
