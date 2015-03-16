package org.dykman.gossamer.webapp;

import org.dykman.gossamer.core.GossamerException;

public class PageNotFoundException extends GossamerException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7882244169442318259L;

	public PageNotFoundException()
	{
	}

	public PageNotFoundException(String message)
	{
		super(message);
	}

	public PageNotFoundException(Throwable cause)
	{
		super(cause);
	}

	public PageNotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
