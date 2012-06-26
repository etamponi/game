package game.core;

import game.configuration.Configurable;
import game.configuration.ConfigurableList;
import game.plugins.constraints.CompatibleWith;

public class TemplateConstrainedList extends ConfigurableList {
	
	public Object constraint;
	
	public TemplateConstrainedList() {
		// DO NOT NEVER EVER USE (NEVER!) Necessary for ConfigurableConverter
		
		setOptionBinding("constraint", "*.template");
		setOptionConstraint("*", new CompatibleWith(this, "constraint"));
	}
	
	public TemplateConstrainedList(Configurable owner, Class content) {
		super(owner, content);
		
		setOptionBinding("constraint", "*.template");
		setOptionConstraint("*", new CompatibleWith(this, "constraint"));
	}
	
}