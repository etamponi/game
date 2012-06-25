package game.core;

import game.configuration.Configurable;
import game.configuration.ConfigurableList;
import game.plugins.constraints.CompatibleWith;

public class TemplateCompatibleList extends ConfigurableList {
	
	public Object constraint;
	
	public TemplateCompatibleList() {
		// DO NOT NEVER EVER USE (NEVER!) Necessary for ConfigurableConverter
		
		setOptionBinding("constraint", "*.template");
		setOptionConstraint("*", new CompatibleWith(this, "constraint"));
	}
	
	public TemplateCompatibleList(Configurable owner, Class content) {
		super(owner, content);
		
		setOptionBinding("constraint", "*.template");
		setOptionConstraint("*", new CompatibleWith(this, "constraint"));
	}
	
}