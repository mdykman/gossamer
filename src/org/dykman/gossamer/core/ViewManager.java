package org.dykman.gossamer.core;

public interface ViewManager {
	public void setView(String view);
	public String getView();
	public int getStatus();
	public void setStatus(int status);
	public void redirect(String url);
	public String getRedirectUrl();
}
