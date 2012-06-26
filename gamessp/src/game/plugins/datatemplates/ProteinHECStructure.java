package game.plugins.datatemplates;

import java.util.LinkedList;

public class ProteinHECStructure extends SequenceTemplate {
	private static final LinkedList<String> types = new LinkedList<>();
	
	static {
		for (String type: "H E C".split(" "))
			types.add(type);
	}
	
	public ProteinHECStructure() {
		atom = new LabelTemplate();
		for (String type: types)
			atom.setOption("labels.add", type);
		
		setInternalOptions("atom");
	}
	
}
