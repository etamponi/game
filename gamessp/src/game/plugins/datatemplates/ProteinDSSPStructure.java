package game.plugins.datatemplates;

import java.util.LinkedList;

public class ProteinDSSPStructure extends SequenceTemplate {
	
	private static final LinkedList<String> types = new LinkedList<>();
	
	static {
		for (String aminoacid: "H,B,E,G,I,T,S, ".split(","))
			types.add(aminoacid);
	}
	
	public ProteinDSSPStructure() {
		atom = new LabelTemplate();
		atom.setOption("labels", types);
		
		setInternalOptions("atom");
	}
}
