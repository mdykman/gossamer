package org.dykman.gossamer.core;

import java.util.Map;

public interface RequestProcessor
{
	static final Object RESPONSE_COMPLETE = new Object();

	public void processRequest(PageController pg,String scriptPath, String anon,Map<String,String> params);
	public Object processRequest(String scriptPath, Map<String,String> params);
}
