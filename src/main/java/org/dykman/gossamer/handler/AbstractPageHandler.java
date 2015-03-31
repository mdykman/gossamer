package org.dykman.gossamer.handler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.dykman.gossamer.core.RequestProcessor;
import org.dykman.gossamer.core.ViewManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public abstract class AbstractPageHandler implements ApplicationContextAware
{
	protected ApplicationContext applicationContext;
	protected Map<String,Object> page
		= new LinkedHashMap<String, Object>();
	
	String defaultView;
	
	public void setApplicationContext(ApplicationContext applicationContext)
    {
    	this.applicationContext = applicationContext;
    }

	protected ViewManager getViewManger()
	{
		return (ViewManager) applicationContext.getBean("viewManager");

	}
	protected String getDate()
	{
		Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(cal.getTime());
	}
	protected RequestProcessor getRequestProcessor()
	{
		return (RequestProcessor) applicationContext
			.getBean("requestProcessor");
	}
	protected void addToPage(String key,Object content)
	{
		page.put(key, content);
	}
	
	protected Map<String,Object> deepCopy(Map<String,Object> m)
	{
		// use linkedhashmap to keep the intact in order... 
		// although it should not matter
		return new LinkedHashMap<String, Object>(m);
	}
	
	protected Map<String, String> emptyParams()
	{
		return new HashMap<String, String>();
	}
	protected Map<String,Object> getPage()
	{
		return page;
	}
	protected void setView()
	{
		setView(defaultView);
	}
	protected void setView(String view)
	{
		getViewManger().setView(view);
	}

	public abstract Object render(Map<String, String> params);
	
	public void setDefaultView(String defaultView)
    {
    	this.defaultView = defaultView;
    }

}
