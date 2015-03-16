package org.dykman.gossamer.webapp;

import javax.servlet.http.HttpServletRequest;

import org.dykman.gossamer.handler.Decorator;

public class ServletRequestDecorator implements Decorator
{

	private javax.servlet.http.HttpServletRequest request;
	public void decorate(Object o)
	{
		if(o instanceof ServletRequestAware)
		{
			((ServletRequestAware)o).setRequest(request);
		}
	}
	
	
	public void setServletRequest(HttpServletRequest request)
    {
    	this.request = request;
    }

}
