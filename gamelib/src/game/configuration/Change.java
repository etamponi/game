package game.configuration;

import java.util.Arrays;
import java.util.List;

public class Change {
	
	private String path;
	
	public Change(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}
	
	public boolean pathContains(String element) {
		List<String> tokens = Arrays.asList(path.split("\\."));
		return tokens.contains(element);
	}
	
}
