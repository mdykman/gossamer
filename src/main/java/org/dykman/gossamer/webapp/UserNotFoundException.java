package org.dykman.gossamer.webapp;

public class UserNotFoundException extends NotFoundException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7382753959608594139L;

	public UserNotFoundException()
	{
	}

	public UserNotFoundException(String message)
	{
		super(message);
	}

	public UserNotFoundException(Throwable cause)
	{
		super(cause);
	}

	public UserNotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
