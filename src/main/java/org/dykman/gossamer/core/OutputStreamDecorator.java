package org.dykman.gossamer.core;

import java.io.OutputStream;

import org.dykman.gossamer.handler.Decorator;

public class OutputStreamDecorator implements Decorator
{

	private OutputStream outputStream;

	public void setOutputStream(OutputStream out)
    {
    	this.outputStream = out;
    }

	public void decorate(Object o)
	{
		if(o instanceof OutputStreamAware)
		{
			((OutputStreamAware)o).setOutputStream(outputStream);
		}
	}

}
