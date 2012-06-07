package game.editorsystem.constraints;

import game.editorsystem.Editor;
import game.plugins.Constraint;

public class CanEditConstraint implements Constraint<Editor> {
	
	private Class type;
	
	public CanEditConstraint(Class type) {
		this.type = type;
	}

	@Override
	public boolean isValid(Editor o) {
		return o.canEdit(type);
	}

}
