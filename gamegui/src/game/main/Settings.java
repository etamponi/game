package game.main;

import game.plugins.PluginManager;
import game.plugins.editors.graph.BlockNode;

public class Settings {

	private static Settings instance = new Settings();
	
	private PluginManager manager = new PluginManager();
	
	private BlockNode dragging;
	
	private Settings() {
		
	}
	
	public static Settings getInstance() {
		return instance;
	}
	
	public PluginManager getPluginManager() {
		return manager;
	}
	
	public BlockNode getDragging() {
		return dragging;
	}
	
	public void setDragging(BlockNode node) {
		this.dragging = node;
	}
	
}
