package org.dykman.gossamer.webapp;

public class PermanentRedirectException extends RedirectException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7509724554629205100L;

	public PermanentRedirectException(String url)
	{
		super(url, 301);
	}

}
