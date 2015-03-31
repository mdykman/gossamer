package org.dykman.gossamer.webapp;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.dykman.gossamer.handler.Decorator;
public class RequestPathDecorator implements Decorator, ServletRequestAware

{
	ServletRequest request;

	public void setRequest(ServletRequest request)
	{
		this.request = request;
	}
	public void decorate(Object o)
	{
		if(o instanceof RequestPathAware)
		{
			HttpServletRequest req = (HttpServletRequest)request;
			((RequestPathAware)o).setRequestPath(req.getPathInfo());
		}
	}

}
