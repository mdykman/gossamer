package org.dykman.gossamer.webapp.cookie;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dykman.gossamer.webapp.ServletRequestHolder;
import org.dykman.gossamer.webapp.ServletResponseHolder;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class CookieManagerImpl implements CookieManager, ApplicationContextAware {
	HttpServletRequest request;
	HttpServletResponse response;
	ApplicationContext context;
	boolean bootstrapped = false;
	
	public Map<String,Cookie> cookies = new HashMap<String, Cookie>();
	
	
	public String domain;

	public CookieManagerImpl() {
	}

	public String get(String name) {
		return getCookie(name);
	}
	public void set(String name, String value) {
		this.setCookie(name, value);
	}

	protected void bootstrap() {
		bootstrapped = true;
		ServletRequestHolder rqh = (ServletRequestHolder) context.getBean("requestHolder");
		request = rqh.getRequest();

		ServletResponseHolder rsh = (ServletResponseHolder) context.getBean("responseHolder");
		response = rsh.getServletResponse();
		
		Cookie [] cc = request.getCookies();
		for(Cookie c : cc) {
			cookies.put(c.getName(), c);
		}
	}
	public void setCookie(String name, String value, int expiry) {
		if(!bootstrapped) bootstrap();
		Cookie c = new Cookie(name,value);
		c.setMaxAge(expiry);
		cookies.put(name, c);
		response.addCookie(c);
	}
	
	public void setCookie(String name, String value) {
		this.setCookie(name, value,0);
	}

	public String getCookie(String name) {
		String result = null;
		if(!bootstrapped) bootstrap();
		Cookie c = cookies.get(name);
		if(c != null) {
			result = c.getValue();
		}
		return result;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		this.context = context;
	}
}
