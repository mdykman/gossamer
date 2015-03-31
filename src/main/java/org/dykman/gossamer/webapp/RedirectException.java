package org.dykman.gossamer.webapp;

import org.dykman.gossamer.core.ResponseException;

public abstract class RedirectException extends ResponseException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4440036451387910727L;
	private String url;
	private int status;
	public RedirectException(String url, int status)
	{
		this.url = url;
		this.status = status;
	}
	public String getUrl()
    {
    	return url;
    }
	public int getStatus()
    {
    	return status;
    }

}
