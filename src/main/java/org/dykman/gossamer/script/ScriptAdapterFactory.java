package org.dykman.gossamer.script;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ScriptAdapterFactory implements ApplicationContextAware
{
	ApplicationContext applicationContext;
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	public ScriptAdapter createScriptAdapter(String ext) {
		String label = ext + "ScriptAdapter";
		ScriptAdapter adapter;
		if(applicationContext.containsBean(label)) 
			adapter =  (ScriptAdapter) applicationContext.getBean(label);
		else 
			adapter = (ScriptAdapter) applicationContext.getBean(
				"defaultScriptAdapter");

		adapter.setScriptEngine(ext);
		return adapter;
	}
}
