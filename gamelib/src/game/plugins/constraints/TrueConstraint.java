package game.plugins.constraints;

import game.plugins.Constraint;

public class TrueConstraint implements Constraint {
	
	static final TrueConstraint instance = new TrueConstraint();
	
	public static TrueConstraint getInstance() {
		return instance;
	}

	@Override
	public boolean isValid(Object o) {
		return true;
	}

}
