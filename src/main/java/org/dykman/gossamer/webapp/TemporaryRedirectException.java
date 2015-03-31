package org.dykman.gossamer.webapp;

public class TemporaryRedirectException extends RedirectException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2746105917126377197L;

	public TemporaryRedirectException(String url)
	{
		super(url,302);
	}
}
