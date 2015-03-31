package org.dykman.gossamer.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.script.CompiledScript;

import org.dykman.gossamer.core.GossamerException;
import org.dykman.gossamer.log.LogManager;
import org.dykman.gossamer.util.CompositeReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ScriptHandler implements ApplicationContextAware {
	ApplicationContext applicationContext;

	protected String scriptName;
	protected String scriptType;

	private static Map<String, ScriptCacheEntry> scriptCache = new java.util.concurrent.ConcurrentHashMap<String, ScriptHandler.ScriptCacheEntry>();

	protected Map<String, Object> properties = new HashMap<String, Object>();
	protected Map<String, Object> bindings = new HashMap<String, Object>();

	ScriptAdapter adapter;

	public Set<String> getCachedScripts() {
		return scriptCache.keySet();
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void setScriptType(String scriptType) {
		this.scriptType = scriptType;
		adapter = (ScriptAdapter) ((ScriptAdapterFactory) applicationContext
				.getBean("scriptAdapterFactory"))
				.createScriptAdapter(scriptType);
	}

	
	protected void bind(String name, Object o) {
		bindings.put(name, o);
	}

	protected void defineProperty(String name, Object o) {
		properties.put(name, o);
	}

	protected Object compile(String name, File f) throws Exception {
		if (!f.exists()) {
			throw new GossamerException("script file `" + name + "' not found");
		}
		CompositeReader cr = new CompositeReader(new FileReader(f));
		File init = new File(f.getParentFile(), "_init." + scriptType);
		if (init.exists()) {
			cr.add(new FileReader(init));
		}

		Object code;
		if (adapter.compileHint()) {
			code = compileToByteCode(name, cr);
		} else {
			code = slurp(cr);
		}
		cr.close();
		return code;
	}

	protected CompiledScript compileToByteCode(String name, Reader script)
			throws Exception {
		LogManager logManager = (LogManager) applicationContext
				.getBean("logManager");
		logManager.writeLog("scripts.log", "compiling script " + name);
		try {
			FileReader fr = new FileReader(name);
			CompiledScript cs = adapter.compile(script);
			fr.close();
			logManager.writeLog("scripts.log", name + " compiled successfully");
			return cs;
		} catch (Error e) {
			logManager.writeLog("scripts.log", name + " failed to compile", e);
			throw new GossamerException(e);
		}
	}

	public Object execute(Map<String, String> params) throws Exception {
		bind("properties", properties);
		prepareBindings(params);
		Object script = this.getCompiledScript(scriptName);
		return adapter.invokeScript(script);
	}

	protected String slurp(Reader r) throws IOException {
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = new BufferedReader(r);
		int n;
		char cbuf[] = new char[4096];
		while ((n = reader.read(cbuf)) != -1) {
			buffer.append(cbuf, 0, n);
		}
		reader.close();
		return buffer.toString();
	}

	public Object clearCache(String key) {
		if (key == null) {
			scriptCache.clear();
			return true;
		} else {
			return scriptCache.remove(key);
		}
	}

	public void setScriptName(String f) {
		scriptName = f;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	protected void applyProperties() {
		for (Map.Entry<String, Object> e : properties.entrySet()) {
			defineProperty(e.getKey(), e.getValue());
		}
	}

	protected void applyBindings(Map<String,Object> bindings) {
		bindings.put("globals", bindings.keySet());
		adapter.setGlobalBindings(bindings);

		applyProperties();
	}

	@SuppressWarnings("unchecked")
	protected void prepareBindings(Map<String, String> params) {
		Map<String,Object> bindings;
		if (applicationContext.containsBean("userScriptBindings")) {
			bindings = (Map<String, Object>) applicationContext
					.getBean("userScriptBindings");
		} else {
			bindings = (Map<String, Object>) applicationContext
					.getBean("scriptBindings");

		}
		bindings.putAll(this.bindings);
		bindings.put("args", params);
		applyBindings(bindings);

	}

	protected Object getCompiledScript(String name) throws Exception {
		File sf = new File(name);
		Object script;
		ScriptCacheEntry e;

		e = scriptCache.get(name);

		long lastModified = sf.lastModified();
		if (e != null && e.timestamp == lastModified) {
			script = e.script;
		} else {
			script = compile(name, sf);
			scriptCache.put(name, new ScriptCacheEntry(script, lastModified));
		}
		return script;
	}

	static class ScriptCacheEntry {
		Object script;
		long timestamp;

		ScriptCacheEntry(Object script, long timestamp) {
			this.script = script;
			this.timestamp = timestamp;
		}
	}
}
