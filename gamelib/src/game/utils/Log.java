package game.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;

public class Log {
	
	private static File logsDirectory = null;
	
	public static void setLogsDirectory(String logsDir) {
		File f = new File(logsDir);
		if (!f.exists()) {
			f.mkdirs();
		} else if (!f.isDirectory()) {
			return;
		}
		
		logsDirectory = f;
	}
	
	public static void write(Object prefix, String format, Object... args) {
		if (logsDirectory == null)
			return;
		
		String fileName = logsDirectory.getAbsolutePath() + "/logging_" + prefix + ".log.txt";
		
		writeLog(fileName, String.format(format, args));
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

}
