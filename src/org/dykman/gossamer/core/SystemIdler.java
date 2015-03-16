package org.dykman.gossamer.core;

import javax.servlet.http.HttpServletRequest;

public class SystemIdler implements Idler
{

	long timeout;
	boolean suspended = false;
	
	public void setRequest(HttpServletRequest request) {
		
	}
	public void setTimeout(long timeout)
	{
		this.timeout = timeout;
	}
	public void interrupt()
	{
		this.notify();
	}

	public boolean isSuspended() {
		return suspended;
	}
	public void waitFor()
	{
		synchronized(this)
		{
			try
			{
				suspended = true;
				this.wait(timeout);
			}
			catch(InterruptedException e)
			{
					
			}
		}
	}

}
