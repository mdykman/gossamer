package org.dykman.gossamer.controller;

import java.util.List;

public class SimpleParallelController implements Controller
{
	public int timeout = 2000;

	public void invoke(List<Runnable> runnables) {
		Scatter scatter = new Scatter(timeout);
		scatter.run(runnables.toArray(new Runnable[runnables.size()]));
	}

	public int getTimeout() {
    	return timeout;
    }
	
	public void setTimeout(int timeout) {
    	this.timeout = timeout;
    }

	static class Scatter {
		int timeout;
		int counter = 0;
		
		Scatter(int timeout) {
			this.timeout = timeout;
		}
		
		public void run(Runnable[] runnables) {
			counter = runnables.length;
			Thr[] tt = factory(runnables);
			for(Thr t : tt) {
				t.start();
			}
			try {
				synchronized(this) {
					this.wait(timeout);
				}
			} catch(InterruptedException e) {
				// ignore
			}
		}

		private Thr[] factory(Runnable [] rr) {
			Thr[] tt = new Thr[counter];
			int i = 0;
			for(Runnable r : rr) {
				tt[i++] = new Thr(r,this);
			}
			return tt;
		}
	}
	
	static class Thr extends Thread {
		Runnable r;
		Scatter scatter;
		Thr(Runnable r,Scatter scatter) {
			this.r =r;
			this.scatter = scatter;
		}
		@Override
		public void run() {
			try {
				r.run();
			}
			catch(Exception e) {
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
