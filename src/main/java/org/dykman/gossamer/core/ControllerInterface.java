package org.dykman.gossamer.core;

import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ControllerInterface implements ApplicationContextAware  
{
	ApplicationContext  applicationContext;
	Controller controller;
	
	public void setController(Controller controller)
	{
		this.controller = controller;
	}

	public void setApplicationContext(ApplicationContext ac)
	{
		applicationContext = ac;
	}
	
	public String createPageIdentifier()
	{
		return controller.createPageIdentifier();
	}

	public ResponseBean[] pollQueue(String pageIdentifier,int n)
		throws Exception
	{
		ResponseBean[] rr = controller.pollQueue(pageIdentifier, n);
		return rr;
	}

	
	public ResponseBean[] processAndFetch(String pageIdentifier, 
			String component, String handler,String anon, 
			Map<String,String> params, int seq)
		throws Exception
	{
		return controller.processAndFetch(
			pageIdentifier, component, handler, anon, params, seq);
	}

	public void processRequest(
			String pageIdentifier,
			String component, 
			String func,
			String anon,
			Map<String,String> params)
	{
		controller.processRequest(pageIdentifier, component, func, anon, params);
	}
	

}
