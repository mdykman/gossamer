package org.dykman.gossamer.webapp;

import javax.servlet.http.HttpServletRequest;

import org.dykman.gossamer.handler.Decorator;

public class PathInfoDecorator implements Decorator
{
	ServletRequestHolder requestHolder;
	
	public void setRequestHolder(ServletRequestHolder requestHolder)
	{
		this.requestHolder = requestHolder;
	}
	public void decorate(Object o)
	{
		if(o instanceof PathInfoAware)
		{
			HttpServletRequest request = (HttpServletRequest)requestHolder.getRequest();
			if(request != null)
			{
				((PathInfoAware)o).setPathInfo(request.getPathInfo());
			}
		}
	}
}
