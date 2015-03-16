package org.dykman.gossamer.webapp;

import org.dykman.gossamer.core.ResponseException;

public class NotFoundException extends ResponseException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8164820930014309290L;

	public NotFoundException()
	{
	}

	public NotFoundException(String message)
	{
		super(message);
	}

	public NotFoundException(Throwable cause)
	{
		super(cause);
	}

	public NotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
