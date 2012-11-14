package game.configuration.listeners;

import game.configuration.Listener;
import game.configuration.IObject;
import game.configuration.Property;

public class PropertyBinding extends Listener {
	
	private final Property master;

	public PropertyBinding(IObject root, String masterPath, String... slavePaths) {
		master = new Property(root, masterPath);
		
		getListenedPaths().add(master);
		
		for(String slavePath: slavePaths) {
			if (slavePath.split(".").length >= IObject.MAXIMUM_CHANGE_PROPAGATION)
				System.err.println("Warning: change propagation will be incomplete");
			
			getSlaves().add(new Property(root, slavePath));
			
			if (!slavePath.contains("."))
				continue;
			String prefix = slavePath.substring(0, slavePath.lastIndexOf('.'));
			getListenedPaths().add(new Property(root, prefix));
		}
	}

	@Override
	public void action(Property changedPath) {
		Object masterContent = master.getContent();
		if (changedPath.isPrefix(master, false)) {
			for(Property slave: getSlaves())
				slave.setContent(masterContent);
		} else {
			for(Property slave: getSlaves()) {
				if (changedPath.isPrefix(slave, false))
					slave.setContent(masterContent);
			}
		}
	}

}
