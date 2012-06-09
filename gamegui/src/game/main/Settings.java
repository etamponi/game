/*******************************************************************************
 * Copyright (c) 2012 Emanuele Tamponi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Emanuele Tamponi - initial API and implementation
 ******************************************************************************/
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
