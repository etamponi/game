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
package game.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;

public class Log {
	
	private static File logsDirectory = null;
	
	private static String currentExperiment = null;
	
	public static void setLogsDirectory(String logsDir) {
		File f = new File(logsDir);
		if (!f.exists()) {
			f.mkdirs();
		} else if (!f.isDirectory()) {
			System.err.println("Could not initialize logs directory! (" + logsDir + ")");
			return;
		}
		
		logsDirectory = f;
	}
	
	public static void setCurrentExperiment(String name) {
		currentExperiment = name;
		if (name != null) {
			File dir = new File(getCompleteDirectory());
			if (!dir.exists()) {
				dir.mkdirs();
			} else if (!dir.isDirectory()) {
				System.err.println("Could not initialize experiment log directory! (" + dir.getPath() + ")");
			}
		}
	}
	
	public static void write(Object prefix, String format, Object... args) {
		if (logsDirectory == null)
			setLogsDirectory("logs/");
		
		String fileName = getCompleteDirectory() + "/logging_" + prefix + ".log.txt";
		
		writeLog(fileName, String.format(format, args));
	}
	
	private static String getCompleteDirectory() {
		if (logsDirectory == null)
			setLogsDirectory("logs/");
		String ret = logsDirectory.getAbsolutePath();
		if (currentExperiment != null)
			ret += "/" + currentExperiment;
		return ret;
	}

	public static void writeTime(Object prefix, String format, Object... args) {
		write(prefix, DateFormat.getDateTimeInstance().format(new Date()) + ": " + format, args);
	}

	private static void writeLog(String fileName, String format) {
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(new File(fileName), true), true);
			System.out.println(format);
			writer.println(format);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/*
	public static class Logger implements Observer {
		
		private LongTask task;
		
		public Logger(LongTask task) {
			this.task = task;
			task.addObserver(this);
		}

		@Override
		public void update(Observable o, Object arg) {
			if (arg instanceof LongTaskUpdate) {
				Log.write(task, "%6.2f%%: %s", task.getCurrentPercent()*100, task.getCurrentMessage());
			}
		}
		
		public void stop() {
			task.deleteObserver(this);
		}
		
		public void start() {
			task.addObserver(this);
		}

		@Override
		protected void finalize() throws Throwable {
			stop();
		}
		
	}
	*/
}
