package org.dykman.gossamer.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.dykman.gossamer.core.GossamerException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ScriptBeanFactory implements ApplicationContextAware
{
	private String scriptBase;
	private ApplicationContext applicationContext;

	private static Map<String,ScriptCacheEntry> scriptCache =
		new HashMap<String,ScriptCacheEntry>();

	public void setScriptBase(String scriptBase) {
    	this.scriptBase = scriptBase;
    }

	public void setApplicationContext(ApplicationContext context) {
		applicationContext = context;
	}
	
	public Object compile(ScriptEngine engine,File path)
		throws Exception {
		if(engine instanceof Compilable) {
			Reader reader = new FileReader(path);
			Object res =  ((Compilable) engine).compile(reader);
			reader.close();
			return res;
		}
		else
			return slurp(path);
	}
	
	protected File mkpath(String path)
	{
		File scriptFile = new File(scriptBase);
		scriptFile = new File(scriptFile,path);
		if(!scriptFile.exists())
		{
			return null;
		}
		return scriptFile;
	}
	
	protected Object getCompiledScript(
			ScriptEngine engine, String script)
		throws Exception
	{
		File scriptFile = mkpath(script);
		if(scriptFile == null) return null;
		Object cs;
		ScriptCacheEntry e;

		System.out.println("ScriptBeanFactory.getCompiledScript(" + engine.toString() + ", " + script + ")");
		
		synchronized (scriptCache)
		{
			e = scriptCache.get(script);
		}

		if(e != null && e.timestamp == scriptFile.lastModified()) {
			cs = e.script;
		}
		else {
			cs = compile(engine, scriptFile);

			synchronized (scriptCache)
			{
				scriptCache.put(script,
					new ScriptCacheEntry(cs,scriptFile.lastModified()));
			}
		}
		return cs;
	}

	public Object createBean(String script)
		throws Exception
	{
		String [] aa = script.split("[.]");
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByExtension(aa[aa.length-1]);

		Object cs = getCompiledScript(engine, script);
		if(cs == null)
			throw new GossamerException("unable to load script: " +script);

		Object bean;
		Bindings bindings = engine.createBindings();
		bindings.put("factory", applicationContext);
		if(cs instanceof CompiledScript) {
			bean = ((CompiledScript)cs).eval(bindings);
		}
		else {
System.out.println("trying to evavluate" + cs.toString());			
			engine.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
			bean = engine.eval(cs.toString());
		}

		org.codehaus.groovy.reflection.ClassInfo.clearModifiedExpandos(); 
		groovy.lang.GroovySystem.getMetaClassRegistry().removeMetaClass(org.codehaus.groovy.reflection.ClassInfo.getClassInfo(cs.getClass()).getMetaClass(cs).getClass());

		return bean;
	}
	
	protected String slurp(File f) 
		throws IOException
	{
		if(f == null) return null;
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = new BufferedReader(
				new FileReader(f));
		String l;
		while((l = reader.readLine())!= null)
		{
			buffer.append(l).append('\n');
		}
		reader.close();
		return buffer.toString();
	}
	
	static class ScriptCacheEntry
	{
		Object script;
		long timestamp;
		ScriptCacheEntry(Object script, long timestamp)
		{
			this.script = script;
			this.timestamp = timestamp;
		}
	}
}
