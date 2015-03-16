package org.dykman.gossamer.script;

import java.util.Map;

import org.dykman.gossamer.core.RequestProcessor;
import org.dykman.gossamer.core.RequestProcessorAware;

public class RuntimeAgent implements RequestProcessorAware
{
	RequestProcessor requestProcessor;
	
	public Object run(Map<String,String> params)
	{
//		String type = params.get("type");
//		File f = FileUtils.tempFile(params.get("text"), type);
		
//		return requestProcessor.processRequest(params.ge, type, params);
		return null;
	}

	public void setRequestProcessor(RequestProcessor requestProcessor)
    {
    	this.requestProcessor = requestProcessor;
    }
}
