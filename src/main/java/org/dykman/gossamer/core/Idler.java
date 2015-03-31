package org.dykman.gossamer.core;

import javax.servlet.http.HttpServletRequest;

public interface Idler
{
	public void waitFor() throws Exception;
	public void interrupt();
	public boolean isSuspended();
	public void setRequest(HttpServletRequest request);

}
