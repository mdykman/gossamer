package org.dykman.gossamer.core;

public class ResponseBean
{
	private String component;
	private String method;
	
	private Object object;
	
	public ResponseBean(String component,String method, Object object)
	{
		this.component = component;
		this.method = method;
		this.object = object;
		
	
	}

	public ResponseBean()
	{
	
	}

	public String getComponent()
    {
    	return component;
    }

	public void setComponent(String component)
    {
    	this.component = component;
    }

	public String getMethod()
    {
    	return method;
    }

	public void setMethod(String method)
    {
    	this.method = method;
    }

	public Object getObject()
    {
    	return object;
    }

	public void setObject(Object object)
    {
    	this.object = object;
    }
}
