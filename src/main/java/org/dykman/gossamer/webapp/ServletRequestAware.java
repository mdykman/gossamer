package org.dykman.gossamer.webapp;

import javax.servlet.ServletRequest;

public interface ServletRequestAware
{
	public void setRequest(ServletRequest request);
}
