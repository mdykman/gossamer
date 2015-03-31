package org.dykman.gossamer.webapp;

import javax.servlet.http.HttpServletRequest;

import org.dykman.gossamer.handler.ResponseWriter;

public class ResponseWriterFactory
{
	ServletRequestHolder requestHolder;
	ServletResponseHolder responseHolder;
	
	
	public ResponseWriter createResponseWriter()
	{
		
		WebResponseWriter writer = new WebResponseWriter(
				(HttpServletRequest)requestHolder.getRequest(),responseHolder.getServletResponse());
		return writer;
	}


	public ServletRequestHolder getRequestHolder()
    {
    	return requestHolder;
    }


	public void setRequestHolder(ServletRequestHolder requestHolder)
    {
    	this.requestHolder = requestHolder;
    }


	public ServletResponseHolder getResponseHolder()
    {
    	return responseHolder;
    }


	public void setResponseHolder(ServletResponseHolder responseHolder)
    {
    	this.responseHolder = responseHolder;
    }
}
