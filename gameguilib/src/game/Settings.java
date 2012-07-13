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
package game;

import game.plugins.PluginManager;

import java.io.File;

public class Settings {
	
	public static final String CONFIGFILE = "plugins.config.xml";
	public static final String RESULTSDIR = "results";
	
	private static PluginManager manager = new PluginManager();
	
	static {
		File config = new File(CONFIGFILE);
		if (!config.exists())
			manager.saveConfiguration(CONFIGFILE);
		else
			manager.loadConfiguration(CONFIGFILE);
	}
	
	public static PluginManager getPluginManager() {
		return manager;
	}
	
}