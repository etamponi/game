package game.plugins.constraints;

import game.plugins.Constraint;

public class SubclassConstraint implements Constraint<Object> {
	
	private Class[] baseClasses;

	public SubclassConstraint(Class... baseClasses) {
		assert(baseClasses.length > 0);
		this.baseClasses = baseClasses;
	}

	@Override
	public boolean isValid(Object o) {
		for(Class base: baseClasses)
			if (base.isAssignableFrom(o.getClass())) return true;
		return false;
	}

}
