package org.dykman.gossamer.core;

import java.util.Collection;
import java.util.Map;

public interface Controller
{
	public ResponseBean[] processAndFetch(String pageIdentifier, 
			String component, String handler,String anon, 
			Map<String,String> params, int seq)
		throws Exception;

	public ResponseBean[] pollQueue(String pageIdentifier,int n)
		throws Exception;
	public void  processRequest(String pageIdentifier,String component, String handler,String anon,Map<String,String> params);
	
	
	public String createPageIdentifier();

	public void enqueue(PageController pg,ResponseBean response);
	public void enqueue(ResponseBean response);
	
	public PageController getPageController(String name);
	public Collection<PageController> getPageControllers();
	public void interrupt();

	
}
