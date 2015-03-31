package org.dykman.gossamer.core;

import java.util.ArrayList;
import java.util.List;

public class ResponseContainerImpl implements ResponseContainer
{
	List<ResponseBean> rr;
	
	public ResponseContainerImpl()
	{
		rr = null;
	}
	public void enqueue(String component, String method, Object o)
	{
		ResponseBean rb = new ResponseBean(component,method,o);
		enqueue(rb);
	}
	public synchronized void enqueue(ResponseBean rb)
	{
		if(rr == null)
		{
			rr = new ArrayList<ResponseBean>();
		}
		rr.add(rb);
		
	}

	public ResponseBean[] getResponse()
	{
		if(rr != null)
		{
			return rr.toArray(new ResponseBean[rr.size()]);
		}
		return null;
		
	}

}
