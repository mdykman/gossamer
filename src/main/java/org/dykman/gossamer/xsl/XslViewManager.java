package org.dykman.gossamer.xsl;

import org.dykman.gossamer.core.ViewManager;

public class XslViewManager implements ViewManager
{
	protected String view = null;
	protected int status = 0;
	protected String redirectUrl =  null;
	
	public void setStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}
	public void setView(String view)
	{
		this.view = view;
	}
	public String getView()
    {
    	return view;
    }

	 public void redirect(String url) {
	 	setStatus(302);
		this.redirectUrl = url;
	 }
	 public String getRedirectUrl() {
	 	return redirectUrl;
	 }

}
