package org.dykman.gossamer.session;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.WebApplicationContext;

public class UserStateImpl implements UserState, ApplicationContextAware
{
	java.util.Map<String, Object> stuff;
	WebApplicationContext webappContext;
	
	public UserStateImpl() {
		stuff = new java.util.concurrent.ConcurrentHashMap<String, Object>();
		stuff.put("info",new HashMap<String, String>());
	}
	public void set(String s, Object o) {
		stuff.put(s, o);
	}
	public void remove(String string ) {
		stuff.remove(string);
	}
	public Object get(String string ) {
		return stuff.get(string);
	}
	public void setApplicationContext(ApplicationContext wac) {
		webappContext = (WebApplicationContext) wac;
	}
	public void setRequest(HttpServletRequest request) {
		@SuppressWarnings("unchecked")
		java.util.Map<String,String> info = (java.util.Map<String,String>) stuff.get("info");
//		info.put("sessionLabel",webappContext.getServletContext().getSessionCookieConfig.getName());
		info.put("sessionLabel","JSESSIONID");
		info.put("sessionKey",request.getSession().getId());
	}
}
