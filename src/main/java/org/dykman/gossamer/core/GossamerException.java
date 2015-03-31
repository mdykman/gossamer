package org.dykman.gossamer.core;

public class GossamerException extends RuntimeException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6835096002141523623L;

	public GossamerException()
	{
	}

	public GossamerException(String message)
	{
		super(message);
	}

	public GossamerException(Throwable cause)
	{
		super(cause);
	}

	public GossamerException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
