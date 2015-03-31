/**
 * 
 */
package org.dykman.gossamer.script;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.dykman.gossamer.util.FileUtils;
import org.springframework.beans.factory.BeanFactory;

 
public class ScriptShell {
	ScriptEngine engine;
	ScriptAdapter adapter;
	Map<String, Object> bindings = new HashMap<String, Object>();
	BeanFactory beanFactory;
	
    public ScriptShell(BeanFactory beanFactory, String ext) {
		this.beanFactory = beanFactory;
		
		ScriptAdapterFactory factory = (ScriptAdapterFactory) 
			beanFactory.getBean("scriptAdapterFactory");
		
		adapter = factory.createScriptAdapter(ext);
		adapter.setScriptEngine(ext);
	}
	public void set(String k,Object o) {
		bindings.put(k, o);
	}

	public Object exec(String script,Map<String,String> args) 
	
			throws ScriptException {
        try {
	        return eval(FileUtils.readFile(new File(script)),args);
        } catch (IOException e) {
	        throw new ScriptException(e);
        }
	}
	
	@SuppressWarnings("unchecked")
	public Object eval(String script,Map<String,String> args)
		throws ScriptException {
		Map<String, Object> defBind;
		
		if(beanFactory.containsBean("userScriptBindings")) {
			defBind = (Map<String, Object>) beanFactory.getBean("userScriptBindings");
		} else {
			defBind = (Map<String, Object>) beanFactory.getBean("scriptBindings");
		}
		bindings.putAll(defBind);
		if(bindings != null) {
			bindings.put("args", args);
		}
		adapter.setGlobalBindings(bindings);

		return adapter.invokeScript(script);
	}
}