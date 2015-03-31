/**
 * 
 */
package org.dykman.gossamer.spring;

import org.dykman.gossamer.controller.Scatter;
import org.dykman.gossamer.controller.WorkerThreadManager;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

class WorkerScatter implements Scatter {
	int timeout = 5000;
	int counter = 0;
	WorkerThreadManager threadManager;
	
	public void setThreadManager(WorkerThreadManager threadManager) {
		this.threadManager = threadManager;
	}	
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	public void run(Runnable[] runnables) {
		counter = runnables.length;
		Runnable[] tt = factory(runnables);
		threadManager.execute(tt);

		try {
			synchronized(this) {
				if(counter> 0) this.wait(timeout);
			}
		} catch(InterruptedException e) {
			// ignore
		}
		if(counter > 0) {
			System.out.println("" + counter + " outstanding tasks at gather time");
//			tg.interrupt();
		}
	}

	private RunWrapper[] factory(Runnable [] rr) {
		RunWrapper[] tt = new RunWrapper[counter];
		int i = 0;
		RequestAttributes ra = RequestContextHolder.currentRequestAttributes();
		for(Runnable r : rr) {
			tt[i++] = new RunWrapper(r,ra,this);
		}
		return tt;
	}

	static class RunWrapper implements Runnable {
		Runnable r;
		WorkerScatter scatter;
		RequestAttributes ra;
		RunWrapper(Runnable r,RequestAttributes ra,WorkerScatter scatter) {
			this.ra = ra;
			this.r = r;
			this.scatter = scatter;
		}

		public void run() {
			try {
				RequestContextHolder.setRequestAttributes(ra);
				r.run();
			}
			catch(Exception e) {
	// report the exception
				e.printStackTrace();
			}
			finally {
				synchronized(scatter) {
					if(--scatter.counter == 0 ) {
						scatter.notify();
					}
				}
			}
		}
	}
}