package org.dykman.gossamer.webapp;

import java.util.Arrays;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;

public class ServletRequestHolder
{
	private HttpServletRequest servletRequest;

	public ServletRequest getServletRequest()
    {
    	return servletRequest;
    }

	public void setServletRequest(HttpServletRequest servletRequest)
    {
    	this.servletRequest = servletRequest;
    }
	
	public HttpServletRequest getRequest()
	{
		return servletRequest;
	}
	public String getHandlerPathInfo()
	{
		String r = "";
		HttpServletRequest req = servletRequest;
		String p = req.getPathInfo();
		String ss[] = p.split("[/]");
		if(ss.length > 3) {
			ss = Arrays.copyOfRange(ss, 3, ss.length);
			r =  StringUtils.arrayToDelimitedString(ss, "/");
		}
		return  r;
	}
}
