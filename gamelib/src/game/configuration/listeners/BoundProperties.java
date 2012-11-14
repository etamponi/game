package game.configuration.listeners;

import game.configuration.ChangeListener;
import game.configuration.IObject;
import game.configuration.Property;

public class BoundProperties extends ChangeListener {
	
	public BoundProperties(IObject root, String... propertyNames) {
		for(String name: propertyNames)
			getSlaves().add(new Property(root, name));
	}

	@Override
	public void onChange(Property changedPath) {
		// Does nothing
	}

}
