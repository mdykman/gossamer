package org.dykman.gossamer.core;

import java.util.LinkedList;
import java.util.List;

import org.springframework.context.ApplicationContext;


public class PageController
{
	ApplicationContext applicationContext;
	private List<ResponseBean> queue = new  LinkedList<ResponseBean>();
	private Idler idler = null;	

	public void setApplicationContext(ApplicationContext context)
	{
		this.applicationContext = context;
	}
	
	public void enqueue(ResponseBean response) 
	{
		synchronized (queue) {
			queue.add(response);
		}
	}
	public void interrupt()
	{
		Idler standby = idler;
		if(standby != null)
		{
			standby.interrupt();
		}
	
	}
	
	
	public ResponseBean[] pollQueue(int n) 
		throws Exception
	{
		return prepareResponse();
	}
	
	protected ResponseBean[] prepareResponse()
	{
		synchronized (queue)
		{
			if(queue.size() > 0)
			{
				ResponseBean[] rr = 
					queue.toArray(new ResponseBean[queue.size()]);
				queue.clear();
				return rr;
			}
		}
		return null;
	}
	public Object dequeue()
	{
		ResponseBean[] rr;
		
		try
		{
			rr = this.pollQueue(0);
		}
		catch(Exception e)
		{
System.err.println("this should not happen in this context");	
e.printStackTrace();
			return null;
		}
		
		if(rr == null || rr.length == 0)
		{
			return null;
		}
		else if(rr.length == 1)
		{
			return rr[0].getObject();
		}
		else // i don't think anything triggers this branch.. i don't think i need it
		{
			Object[] arr = new Object[rr.length];
			int i =0;
			for(ResponseBean r : rr)
			{
				arr[i++] = r.getObject();
			}
			return arr;
		}
	}

}
