/*******************************************************************************
 * Copyright (c) 2012 Emanuele.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Emanuele - initial API and implementation
 ******************************************************************************/
package game.main;

import game.configuration.PluginManager;
import game.utils.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Settings {
	
	public static final String CONFIGFILE = "plugins.bin";
	public static final String RESULTSDIR = "results";
	public static final String LOGSDIR = "logs";
	
	public static void initialize() {
		File config = new File(CONFIGFILE);
		try {
			FileInputStream inFile = new FileInputStream(config);
			PluginManager.initialize(inFile);
			inFile.close();
		} catch (FileNotFoundException e) {
			PluginManager.initialize();
			PluginManager.getConfiguration().write(config);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.setLogsDirectory(LOGSDIR);
	}
}
