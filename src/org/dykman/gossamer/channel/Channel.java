package org.dykman.gossamer.channel;

import java.util.Map;
import java.util.WeakHashMap;

import org.dykman.gossamer.core.PageController;
import org.dykman.gossamer.core.ResponseBean;

public class Channel
{
	private static final Object ballast = new Object();

	private Map<PageController,Object> queue = new WeakHashMap<PageController,Object>();
	
	private boolean anonymous;

	public Channel(boolean b)
	{
		anonymous = b;
	}
	
	public boolean isAnonymous()
    {
    	return anonymous;
    }

	public Channel()
	{
		this(true);
	}
	public void add(PageController controller)
	{
		queue.put(controller, ballast);
	}

	public boolean isEmpty()
	{
		return queue.isEmpty();
	}
	
	public void broadcast(ResponseBean response)
	{
		for(PageController cc : queue.keySet())
		{
			cc.enqueue(response);
			cc.interrupt();
		}
	}

	public void remove(PageController controller)
	{
		queue.put(controller, ballast);
	}
}
