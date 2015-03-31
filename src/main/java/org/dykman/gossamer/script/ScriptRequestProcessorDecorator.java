package org.dykman.gossamer.script;

import org.dykman.gossamer.core.RequestProcessorAware;
import org.dykman.gossamer.handler.Decorator;

public class ScriptRequestProcessorDecorator implements Decorator
{
	ScriptRequestProcessor scriptRequestProcessor;
	
	public void decorate(Object o)
	{
		if(o instanceof RequestProcessorAware)
		{
			RequestProcessorAware rp = (RequestProcessorAware)o;
			rp.setRequestProcessor(scriptRequestProcessor);
		}
	}

	public void setScriptRequestProcessor(
            ScriptRequestProcessor scriptRequestProcessor)
    {
    	this.scriptRequestProcessor = scriptRequestProcessor;
    }

}
