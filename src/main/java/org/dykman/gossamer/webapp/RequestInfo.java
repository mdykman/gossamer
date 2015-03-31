package org.dykman.gossamer.webapp;

import javax.servlet.http.HttpServletRequest;

import org.dykman.gossamer.location.LocationServiceWrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.maxmind.geoip.Location;


public class RequestInfo implements ApplicationContextAware {
	private HttpServletRequest request;

	private boolean authorized = false;
//	private String authType = null;
	private boolean secure = false;
	private String cypher = null;
	private String pathInfo = "";
//	private String sessionCookieValue = "";
	private String ipAddress = null;
	@SuppressWarnings("unused")
	private ApplicationContext context;
	private Location location = null;
	private LocationServiceWrapper locationService;

	public RequestInfo() {
	}
	
	public void setApplicationContext(ApplicationContext context) {
		this.context = context;
	}

	public void setLocationService(LocationServiceWrapper ls) {
		locationService = ls;
	}

	public Location getGeoIp () {
		String ip = getIpAddress();
		if(location == null && ip!= null) {
			location = locationService.getLocation(ip);
		}
		return location;
	}

	public void setRequestHolder(ServletRequestHolder req) {
		this.request = req.getRequest();
		String s = request.getAuthType();

		if(s != null) {
			authorized = true;
//			authType = s;
		}
		s = (String)request.getAttribute("javax.servlet.request.cipher_suite");
		if(s != null)  {
			secure = true;
			cypher = s;
		}
	}
	
	public void setPathInfo(String pi) {
		this.pathInfo = pi;
	}

	public String getPathInfoString() {
		int sz = pathInfo.length();
		if(sz > 0 && pathInfo.charAt(0) == '/') {
			return sz > 1 ? pathInfo.substring(1) : "";	
		}

		return pathInfo;
	}
	public java.util.Map<String,String> getSessionInfo() {
		java.util.Map <String,String> info = new java.util.HashMap<String,String>();
		info.put("label","JSESSIONID");
		info.put("key",request.getSession().getId());
		return info;
	}

	public String getPathInfo() {
		return pathInfo;
	}

	public boolean getAuthorized() {
		return authorized;
	}
	public boolean getSecure() {
		return secure;
	}
	public String getCypher() {
		return cypher;
	}
	public String getHeader(String key) {
			return request.getHeader(key);
	}
	public String getIpAddress() {
		if(ipAddress == null) {
			String s = request.getHeader("X-Forwarded-For");
			if(s != null && s.indexOf(',') != -1) {
				String [] sp = s.split("[,]");
				// take the first address in the chain
				ipAddress = sp[0].trim();
			} else {
				ipAddress = s;
			}
			if(ipAddress == null) ipAddress = request.getRemoteAddr();
		}
		return ipAddress;
	}

}
