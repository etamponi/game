package game.configuration;

import java.util.LinkedList;

public abstract class Configurable {

	public String name;
	
	private LinkedList<String> boundOptions = new LinkedList<>();
	
	public Configurable() {
		this.name = String.format("%s%4d", getClass().getSimpleName(), hashCode() % 1000);
	}
	
	public <T> T getOption(String optionPath) {
		if (optionPath.isEmpty())
			return null;
		
		int dotIndex = optionPath.indexOf('.');
		try {
			if (dotIndex < 0) {
				return (T)getClass().getField(optionPath).get(this);
			} else {
				Configurable sub = (Configurable)getClass().getField(optionPath.substring(0, dotIndex)).get(this);
				if (sub == null)
					return null;
				else
					return sub.getOption(optionPath.substring(dotIndex+1));
			}
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
