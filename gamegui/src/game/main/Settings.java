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

import game.utils.Log;

import java.io.File;

import com.ios.IObject;
import com.ios.PluginManager;
import com.ios.PluginManager.PluginConfiguration;

public class Settings {
	
	public static final File CONFIGFILE = new File("plugins.bin");
	public static final String RESULTSDIR = "results";
	public static final String LOGSDIR = "logs";
	
	public static void initialize() {
		PluginConfiguration config = null;
		if (CONFIGFILE.exists()) {
			config = IObject.load(CONFIGFILE);
		} else {
			config = new PluginConfiguration();
			config.packages.add("game");
			addPlugins(config);
			config.write(CONFIGFILE);
		}
		PluginManager.initialize(config);
		Log.setLogsDirectory(LOGSDIR);
		
//		IObject.getKryo().addDefaultSerializer(Dataset.class, DatasetSerializer.class);
	}

	private static void addPlugins(PluginConfiguration config) {
		File dir = new File("plugins/");
		if (dir.exists() && dir.isDirectory()) {
			for (File plugin: dir.listFiles()) {
				if (plugin.getName().endsWith(".jar"))
					config.paths.add(plugin);
			}
		}
	}
}
