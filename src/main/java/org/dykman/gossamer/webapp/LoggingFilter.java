package org.dykman.gossamer.webapp;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.dykman.gossamer.log.LogManager;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public abstract class LoggingFilter implements Filter {
	ApplicationContext applicationContext;
	boolean log = false;
	String logFile = "general.log";

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}
	public void setLogFile(String name) {
		logFile = name;
	}
	
	protected void log(String message) {
		if(log) {
			LogManager logManager = 
					(LogManager) applicationContext.getBean("logManager");
			logManager.writeLog(logFile, message);
		}
	}

	public void init(FilterConfig config) throws ServletException {
		ServletContext context = config.getServletContext();
		applicationContext = WebApplicationContextUtils
				.getWebApplicationContext(context);
		String p = config.getInitParameter("log");
		if (p != null && p.equalsIgnoreCase("true")) {
			System.out.println("MappingFilter logging enabled");
			log = true;
		}
	}

}
