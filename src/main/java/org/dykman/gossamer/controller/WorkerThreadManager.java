package org.dykman.gossamer.controller;

import java.util.LinkedList;

public class WorkerThreadManager implements ThreadManager
{
	LinkedList<Runnable> tasks = new LinkedList<Runnable>();
	LinkedList<WorkerThread> sleepingThreads = new LinkedList<WorkerThread>();
	
	int activeThreads = 0;
	int countThreads = 0;
	int minThreads = 10;
	int maxThreads = 75;
	int reserveThreads = 2;
	int timeToLive = 100;
	long timeToHang = 500;
	
	int timeout = 200;
	
	public void init() {
		timeToHang = timeToHang / timeout;
		adjustThreadPool();
	}
	public void execute(Runnable task) {
		WorkerThread t = null;
		synchronized(sleepingThreads) {
			if(sleepingThreads.size() > 0) {
				t = sleepingThreads.pop();
			} else {
				synchronized(tasks) {
					tasks.add(task);
				}
			}
		}
		if(t != null) {
			synchronized(t) {
				t.setRunnable(task);
				t.notify();
			}
		}
		adjustThreadPool();
	}
	
	public void execute(Runnable[] tasks) {
		for(Runnable t : tasks) {
			execute(t);
		}
	}
	
	void done(WorkerThread wt) {
		synchronized(wt) {
			boolean tasked = true;
			
			boolean go = timeToLive > wt.getLifetime();
			if(!go) { // not trying to pick up a task
				wt.setRunning(false);
			}

			if(go) {
				Runnable r = nextTask();
				if(r != null) {
					wt.setRunnable(r);
				} else {
					synchronized(sleepingThreads) {
						sleepingThreads.add(wt);
					}
					tasked = false;
				}
			}

			if(!tasked) sleep(wt);
		}
	}
	
	private void sleep(WorkerThread wt) {
		do {
			try {
				wt.wait(timeout);
			} catch(InterruptedException e) { }
			if(wt.runnable == null) {
				int a = activeThreads + reserveThreads;
				a = a > minThreads ? a : minThreads;
				if(a < countThreads && wt.idleCount++ > timeToHang) {
					synchronized(sleepingThreads) {
						sleepingThreads.remove(wt);
					}
					wt.running = false;
					break;
				}
			}
		} while(wt.runnable == null);
	}
	
	public Runnable nextTask() {
		Runnable result = null;
		synchronized(tasks) {
			if(tasks.size() > 0) {
				result = tasks.pop();
			}
		}
		return result;
	}
	
	public synchronized void adjustRunning(int n) {
		if(n>0) {
			++countThreads;
		} else {
			--countThreads;
		}
	}
	public synchronized void adjustActive(int n) {
		if(n>0) {
			++activeThreads;
		} else {
			--activeThreads;
		}
	}

/*
	private void report(String prompt)
	{
		System.out.println(prompt 
				+ " active threads: " + this.activeThreads 
				+ " count threads: " + this.countThreads);		
		
	}
*/
	private synchronized void addThreads(int n) {
///		report("adding threads " + n);
		int nn = maxThreads - countThreads;
		n = nn < n ? nn : n;
		for(int i = 0;i < n; ++i) {
			new WorkerThread(this).start();
			++countThreads;
		}
//		report("done adding threads");
	}

	private synchronized void adjustThreadPool() {
		int a = activeThreads + reserveThreads;
		a = minThreads > a ? minThreads : a;
		if((a = a - countThreads) > 0) {
			addThreads(a);
		}
	}
	public int getActiveThreads()
    {
    	return activeThreads;
    }
	public int getCountThreads()
    {
    	return countThreads;
    }
	public int getMinThreads()
    {
    	return minThreads;
    }
	public void setMinThreads(int minThreads)
    {
    	this.minThreads = minThreads;
    }
	public int getMaxThreads()
    {
    	return maxThreads;
    }
	public void setMaxThreads(int maxThreads)
    {
    	this.maxThreads = maxThreads;
    }
	public int getReserveThreads()
    {
    	return reserveThreads;
    }
	public void setReserveThreads(int reserveThreads)
    {
    	this.reserveThreads = reserveThreads;
    }
	public int getTimeToLive()
    {
    	return timeToLive;
    }
	public void setTimeToLive(int timeToLive)
    {
    	this.timeToLive = timeToLive;
    }
	public long getTimeToHang()
    {
    	return timeToHang;
    }
	public void setTimeToHang(long timeToHang)
    {
    	this.timeToHang = timeToHang;
    }
}
