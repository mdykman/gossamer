package org.dykman.gossamer.controller;

public class WorkerThread extends Thread
{
	Runnable runnable = null;
	WorkerThreadManager manager;

	boolean running = true;
	int lifetime = 0;
	int idleCount = 0;

	WorkerThread(WorkerThreadManager manager) {
		this.manager = manager;
	}
	@Override
	public void run() {
		while(running) {
			++lifetime;
			if(runnable != null) {
				try {
					manager.adjustActive(1);
					idleCount = 0;
					runnable.run();
				} catch(Exception e) {
					e.printStackTrace();
				}
				finally {
					runnable = null;
					manager.adjustActive(-1);
				}
			} else {
				++idleCount;
			}
			manager.done(this);
		}
manager.adjustRunning(-1);
	}
	
	public void setRunnable(Runnable runnable) {
    	this.runnable = runnable;
    }

	public boolean isRunning()
    {
    	return running;
    }

	public void setRunning(boolean b)
    {
    	this.running = b;
    }

	public int getLifetime() {
    	return lifetime;
    }
	public int getIdleCount() {
    	return idleCount;
    }
}
