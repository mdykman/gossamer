package org.dykman.gossamer.webapp;

import javax.servlet.http.HttpServletResponse;

public interface ServletResponseAware
{
	public void setServletResponse(HttpServletResponse response);
}
