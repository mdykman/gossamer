package org.dykman.gossamer.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Calendar;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SimpleLogManager 
	implements LogManager, ApplicationContextAware {
	ApplicationContext applicationContext;
	protected File logBase;

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	public void setLogBase(String s) {
		logBase = new File(s);
		if(!logBase.exists()) {
System.err.println("specified log directory does not exist: " + s);
			logBase = null;
		}
		if(logBase != null && !logBase.isDirectory()) {
System.err.println("specified log directory is not a directory: " + s);
			logBase = null;
		}
	}
	public void writeLog(String log, String message,Throwable e)  {
		if(logBase != null && logBase.canWrite()) {
			try {
				File l = new File(logBase,log);
				Writer writer = new FileWriter(l, true);
				writeLogImpl(writer,message);
				StackTraceElement[] st = e.getStackTrace();
				for(int i = 0; i < st.length; ++i) {
					writer.write("\t" + st.toString());
				}
				writer.close();
			} catch(IOException ee) {
				System.err.println("error writing to log file: " + ee.getMessage());
				ee.printStackTrace();
			}
		} else {
				System.err.println("failed to write to log file: " + log);
		}
	}
	public void writeLog(String log, String message)  {
		if(logBase != null && logBase.canWrite()) {
			try {
				File l = new File(logBase,log);
				Writer writer = new FileWriter(l, true);
				writeLogImpl(writer,message);
				writer.close();
			} catch(IOException e) {
				System.err.println("error writing to log file: " + log);
				e.printStackTrace();
			}
		} else {
				System.err.println("failed to write to log file: " + log);
		}
	}

	public void writeLogImpl(Writer writer, String message) {
		if(logBase != null & logBase.canWrite()) {
			try {
				DateFormat df = DateFormat.getDateTimeInstance();
				writer.write(df.format(Calendar.getInstance().getTime()) + ": " + message + "\n");
				// writer.close();
			} catch(IOException e) {
				System.err.println("failed to write to log file: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
