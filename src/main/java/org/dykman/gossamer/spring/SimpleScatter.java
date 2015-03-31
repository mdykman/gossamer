/**
 * 
 */
package org.dykman.gossamer.spring;

import org.dykman.gossamer.controller.Scatter;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

class SimpleScatter implements Scatter {
	int timeout = 5000;
	int counter = 0;
	
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	public void run(Runnable[] runnables) {
		counter = runnables.length;
		ThreadGroup tg = new ThreadGroup("scattergroup-" + hashCode());
		Thr[] tt = factory(runnables,tg);
		for(Thr t : tt) {
			t.start();
		}
		try {
			synchronized(this) {
				if(counter> 0) this.wait(timeout);
			}
		} catch(InterruptedException e) {
			// ignore
		}
		if(counter > 0) {
			tg.interrupt();
		}
	}

	private Thr[] factory(Runnable [] rr,ThreadGroup tg) {
		Thr[] tt = new Thr[counter];
		int i = 0;
		RequestAttributes ra = RequestContextHolder.currentRequestAttributes();
		for(Runnable r : rr) {
			tt[i++] = new Thr(r,tg,ra,this);
		}
		return tt;
	}

	static class Thr extends Thread {
		Runnable r;
		SimpleScatter scatter;
		RequestAttributes ra;
		Thr(Runnable r,ThreadGroup tg, RequestAttributes ra,SimpleScatter scatter) {
			super(tg,"scatter");
			this.ra = ra;
			this.r = r;
			this.scatter = scatter;
		}
		@Override
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