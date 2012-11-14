package game.configuration;

import java.util.ArrayList;
import java.util.List;

public abstract class ChangeListener {
	
	private final List<Property> listened = new ArrayList<>();
	private final List<Property> slaves = new ArrayList<>();

	public abstract void onChange(Property changedPath);
	
	protected List<Property> getListenedPaths() {
		return listened;
	}
	
	protected List<Property> getSlaves() {
		return slaves;
	}

	public List<Property> getBoundProperties(Property prefixPath) {
		List<Property> ret = new ArrayList<>();
		for(Property slave: slaves) {
			if (prefixPath.isParent(slave))
				ret.add(slave.getLocalProperty());
		}
		return ret;
	}

	public boolean isListeningOn(Property path) {
		for(Property l: listened) {
			if (path.isPrefix(l, false))
				return true;
		}
		return false;
	}
	
}
