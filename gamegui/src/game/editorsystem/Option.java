package game.editorsystem;

import game.configuration.Configurable;
import game.editorsystem.constraints.CanEditConstraint;
import game.main.Settings;
import game.plugins.PluginManager;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Option {
	
	private Configurable owner;
	private String optionName;
	
	public Option(Configurable owner, String optionName) {
		this.owner = owner;
		this.optionName = optionName;
	}
	
	public Configurable getOwner() {
		return owner;
	}
	
	public String getOptionName() {
		return optionName;
	}
	
	public <T> T getContent() {
		if (optionName.equals("this"))
			return (T)owner;
		else
			return owner.getOption(optionName);
	}
	
	public void setContent(Object content) {
		if (optionName.equals("this"))
			owner = (Configurable)content;
		else
			owner.setOption(optionName, content);
	}
	
	public Class getType() {
		if (optionName.equals("this"))
			return owner.getClass();
		else
			return owner.getOptionType(optionName);
	}
	
	public Set<Object> getCompatibleInstances() {
		if (optionName.equals("this")) {
			Set<Object> ret = new HashSet<>();
			ret.add(owner);
			return ret;
		} else
			return owner.getCompatibleOptionInstances(optionName, Settings.getInstance().getPluginManager());
	}
	
	public boolean isBound() {
		if (optionName.equals("this"))
			return false;
		else
			return !owner.getUnboundOptionNames().contains(optionName);
	}
	
	public Editor getBestEditor() {
		PluginManager manager = Settings.getInstance().getPluginManager();
		
		Set<Editor> editors = manager.getCompatibleInstancesOf(Editor.class, new CanEditConstraint(getType()));
		Iterator<Editor> it = editors.iterator();
		
		if (!it.hasNext())
			return null;
		
		Editor best = it.next();
		int bestDistance = distance(best.getBaseEditableClass(), getType());
		while (it.hasNext()) {
			Editor current = it.next();
			int currDistance = distance(current.getBaseEditableClass(), getType());
			if (currDistance < bestDistance) {
				best = current;
				bestDistance = currDistance;
			}
		}
		return best;
	}
	
	private int distance(Class origin, Class target) {
		Set<Class> currents = new HashSet<>();
		currents.add(target);
		int d = 0;
		while(!currents.contains(origin) && !currents.isEmpty()) {
			currents = findAllParents(currents);
			d++;
		}
		return d;
	}
	
	private Set<Class> findAllParents(Set<Class> currents) {
		Set<Class> parents = new HashSet<>();
		for (Class target: currents) {
			if (target.getSuperclass() != null)
				parents.add(target.getSuperclass());
			for (Class i: target.getInterfaces())
				parents.add(i);
		}
		return parents;
	}

}
