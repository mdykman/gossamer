package org.dykman.gossamer.jetty;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.dykman.gossamer.core.Idler;

public class JettyIdler implements Idler {
	public static final String Jetty7Support = 
		"org.eclipse.jetty.continuation.ContinuationSupport";
	public static final String Jetty6Support = 
		"org.mortbay.jetty.continuation.ContinuationSupport";
	protected static Class<?> support = null;
	protected static Class<?> continuationClass = null;

	Object continuation = null;

	protected static Method factory;
	boolean suspended = false;
	public JettyIdler() {
	}

	static {
		try {
			try {
				support = Class.forName(Jetty7Support);
			} catch(Exception ee) {
				support = Class.forName(Jetty6Support);
			}
			 factory = support.getMethod("getContinuation",
						ServletRequest.class);
		} catch(Exception e) {
			support = null;		
		}
	}
	
	public void setRequest(HttpServletRequest request) {
		if(support != null) try {
			continuation = factory.invoke(null, request);
			continuationClass = continuation.getClass();
		} catch (Throwable ignored) {
// TODO:: more than nothing
		}

	}

	@Override
	public void waitFor() throws Exception {
		try {
			suspended = true;
			Method suspend = continuationClass.getMethod("suspend");
			suspend.invoke(continuation);
		} catch(Exception e) {
			if(e instanceof InvocationTargetException) {
				throw (Exception)e.getCause();
			}
			throw e;
		}
	}

	@Override
	public boolean isSuspended() {
		return suspended;
	}
	@Override
	public void interrupt() {
		try {
			Method expired = continuationClass.getMethod("isExpired");
			Boolean b = (Boolean)expired.invoke(continuation);
			if(!b) {
				Method suspend = continuationClass.getMethod("resume");
				suspend.invoke(continuation);
			}
		} catch(Exception e) {
			if(e instanceof InvocationTargetException) {
				throw new RuntimeException(e.getCause());
			}
			throw new RuntimeException(e);
		}

	}

}
