package org.dykman.gossamer.webapp;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.dykman.gossamer.handler.Decorator;

public class OutputStreamDecorator implements Decorator
{
	private HttpServletResponse httpServletResponse;
	public void decorate(Object o)
	{
		if(o instanceof OutputStreamAware)
		{
			try
			{
				((OutputStreamAware) o).setOutputStream(
						httpServletResponse.getOutputStream());
			}
			catch(IOException e)
			{
				throw new org.dykman.gossamer.core.AbortRequestException(e);
			}
		}
	}
	public void setHttpServletResponse(HttpServletResponse httpServletResponse)
    {
    	this.httpServletResponse = httpServletResponse;
    }
}
