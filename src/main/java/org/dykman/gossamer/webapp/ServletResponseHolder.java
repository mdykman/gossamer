package org.dykman.gossamer.webapp;

import javax.servlet.http.HttpServletResponse;

public class ServletResponseHolder {
	private HttpServletResponse servletResponse;

	public void setServletResponse(HttpServletResponse servletResponse)
    {
    	this.servletResponse = servletResponse;
    }

	public HttpServletResponse getServletResponse()
    {
    	return servletResponse;
    }
	
}
