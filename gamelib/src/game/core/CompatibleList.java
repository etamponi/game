package game.core;

import game.configuration.Configurable;
import game.configuration.ConfigurableList;
import game.plugins.constraints.CompatibleConstraint;

public class CompatibleList extends ConfigurableList {
	
	public Object constraint;
	
	public CompatibleList() {
		// DO NOT NEVER EVER USE (NEVER!) Necessary for ConfigurableConverter
	}
	
	public CompatibleList(Configurable owner, Class content, String slave) {
		super(owner, content);
		
		addOptionBinding("constraint", "*." + slave);
		
		setOptionConstraint("*", new CompatibleConstraint(this, "constraint"));
	}
	
}
