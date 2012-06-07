package game.main;

import game.plugins.PluginManager;

public class Settings {

	private static Settings instance = new Settings();
	
	private PluginManager manager = new PluginManager();
	
	private Settings() {
		
	}
	
	public static Settings getInstance() {
		return instance;
	}
	
	public PluginManager getPluginManager() {
		return manager;
	}
	
}
