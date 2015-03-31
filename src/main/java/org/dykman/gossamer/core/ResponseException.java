package org.dykman.gossamer.core;

public abstract class ResponseException extends GossamerException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3203582059604515244L;

	public ResponseException()
	{
	}

	public ResponseException(String message)
	{
		super(message);
	}

	public ResponseException(Throwable cause)
	{
		super(cause);
	}

	public ResponseException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
