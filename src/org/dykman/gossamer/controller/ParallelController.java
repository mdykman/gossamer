package org.dykman.gossamer.controller;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


public class ParallelController implements Controller, ApplicationContextAware
{
	public int timeout = 2000;

	ApplicationContext applicationContext;
	String scatterRef;

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	public void setScatterRef(String s) {
		this.scatterRef = s;
	}

	public void invoke(List<Runnable> runnables) {
		Scatter scatter = (Scatter) applicationContext.getBean(scatterRef);
		scatter.run(runnables.toArray(new Runnable[runnables.size()]));
	}

	public int getTimeout() {
    	return timeout;
    }
	
	public void setTimeout(int timeout) {
    	this.timeout = timeout;
    }
}
