package org.dykman.gossamer.webapp.rewrite;

import javax.servlet.ServletRequest;

public interface RequestMapper
{
	public String mapRequest(ServletRequest request);
}
