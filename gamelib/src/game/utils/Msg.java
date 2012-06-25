package game.utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Msg {
	
	private static Formatter formatter = new Formatter() {

		@Override
		public String format(LogRecord record) {
			return record.getMessage() + System.lineSeparator();
		}
		
	};

	private static final Logger infoLogger = Logger.getLogger("game.info");
	private static final Logger dataLogger = Logger.getLogger("game.data");
	
	private static File logsDir = null;
	private static Handler infoHandler = null;
	private static Handler dataHandler = null;
	
	private static String prefix = null;
	
	public static void setLogsDirectory(File directory) {
		if (directory == null)
			return;
		
		if (!directory.exists())
			directory.mkdirs();
		if (!directory.isDirectory()) {
			logsDir = null;
			infoLogger.removeHandler(infoHandler);
			dataLogger.removeHandler(dataHandler);
			if (infoHandler != null)
				infoHandler.close();
			if (dataHandler != null)
				dataHandler.close();
			infoHandler = null;
			dataHandler = null;
		} else {
			logsDir = directory;
			if (prefix != null) {
				setLogPrefix(prefix);
			}
		}
	}
	
	public static void setLogPrefix(String prefix) {
		if (logsDir == null)
			return;

		infoLogger.removeHandler(infoHandler);
		dataLogger.removeHandler(dataHandler);
		if (infoHandler != null)
			infoHandler.close();
		if (dataHandler != null)
			dataHandler.close();
		try {
			infoHandler = new FileHandler(logsDir.getAbsolutePath()+"/"+prefix + "_info.log.txt");
			infoHandler.setFormatter(formatter);
			dataHandler = new FileHandler(logsDir.getAbsolutePath()+"/"+prefix + "_data.log.txt");
			dataHandler.setFormatter(formatter);
			infoLogger.addHandler(infoHandler);
			dataLogger.addHandler(dataHandler);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String info(String format, Object... args) {
		return writeLog(infoLogger, format, args);
	}
	
	public static String data(String format, Object... args) {
		return writeLog(dataLogger, format, args);
	}
	
	private static String writeLog(Logger logger, String format, Object... args) {
		String ret = String.format(format, args);
		logger.info(ret);
		return ret;
	}
	
}
