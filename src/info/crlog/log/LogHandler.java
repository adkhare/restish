package info.crlog.log;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * LogHandler outputs contents to a specified file
 */
public class LogHandler extends Handler {

	FileOutputStream fileOutputStream;
	PrintWriter printWriter;

	public LogHandler(String filename) {
		super();

		// check input parameter
//		if (filename == null ? "" == null : filename.equals(""))
//			filename = new PropertiesManager("conf/settings").getProperty("logFile");

		try {
			// initialize the file
			fileOutputStream = new FileOutputStream(filename);
			printWriter = new PrintWriter(fileOutputStream);
			 setFormatter(new SimpleFormatter());
		}
		catch (Exception e) {
			// implement exception handling...
		}
	}

	/* (non-API documentation)
	 * @see java.util.logging.Handler#publish(java.util.logging.LogRecord)
	 */
	public void publish(LogRecord record) {
		// ensure that this log record should be logged by this Handler
		if (!isLoggable(record))
			return;

		// Output the formatted data to the file
		printWriter.println(getFormatter().format(record));
	}

	/* (non-API documentation)
	 * @see java.util.logging.Handler#flush()
	 */
	public void flush() {
		printWriter.flush();
	}

	/* (non-API documentation)
	 * @see java.util.logging.Handler#close()
	 */
	public void close() throws SecurityException {
		printWriter.close();
	}
}