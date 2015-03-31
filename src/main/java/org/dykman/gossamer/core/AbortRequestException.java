package org.dykman.gossamer.core;

public class AbortRequestException extends GossamerException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2395332197775183273L;

	public AbortRequestException()
	{
	}

	public AbortRequestException(String message)
	{
		super(message);
	}

	public AbortRequestException(Throwable cause)
	{
		super(cause);
	}

	public AbortRequestException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
