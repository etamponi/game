package game.configuration.listeners;

import game.configuration.Listener;
import game.configuration.IObject;
import game.configuration.Property;

public class BoundProperties extends Listener {
	
	public BoundProperties(IObject root, String... propertyNames) {
		for(String name: propertyNames)
			getSlaves().add(new Property(root, name));
	}

	@Override
	public void action(Property changedPath) {
		// Does nothing
	}

}
