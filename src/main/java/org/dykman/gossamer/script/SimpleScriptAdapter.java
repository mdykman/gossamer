package org.dykman.gossamer.script;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.dykman.gossamer.handler.ResponseWriter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SimpleScriptAdapter implements ScriptAdapter,
		ApplicationContextAware {
	protected ScriptEngineManager manager = new ScriptEngineManager();
	ScriptEngine engine = null;
	protected ApplicationContext applicationContext;
	protected String extension;
	Writer writer = new PrintWriter(System.out);

	boolean compileHint = true;

	public Object toJava(Object o) {
		return o;
	}
	
	public Bindings getBindings() {
		return engine.getBindings(ScriptContext.GLOBAL_SCOPE);
	}

	protected Bindings createBindings(int type) {
		return null;
	}
	public void setGlobalBindings(java.util.Map<String, Object> bb) {
		java.util.Map<String, Object> all = engine.getBindings(ScriptContext.GLOBAL_SCOPE);
		
		if(all == null) {
			Bindings bi = engine.createBindings();
			bi.putAll(bb);
			engine.setBindings(bi, ScriptContext.GLOBAL_SCOPE);
			all = bi;
		} else {
			all.putAll(bb);
		}
		
		if(all.containsKey("writer")) {
			ResponseWriter rs = (ResponseWriter) all.get("writer");
			ScriptContext context = engine.getContext();
			try {
				writer = rs.getWriter();
				context.setWriter(writer);
			}catch(IOException e) {
				System.err.println("error while setting output stream: " + e.getLocalizedMessage());
			}
		}
	}

	public String getType() {
		return extension;
	}

	public void setScriptEngine(String ext) {
		extension = ext;
		engine = manager.getEngineByExtension(ext);
	}

	public CompiledScript compile(Reader reader) throws ScriptException {
		return ((Compilable) engine).compile(reader);
	}

	public Object invokeScript(Object script) throws ScriptException {
		return toJava(execute(script));
	}

	public boolean compileHint() {
		return compileHint && engine instanceof Compilable;
	}

	public void setCompileHint(boolean b) {
		compileHint = b;
	}

	protected Object interpret(String script, ScriptContext context)
			throws ScriptException {
		// engine.
		return engine.eval(script.toString(), context);
	}

	protected Object exec(CompiledScript script, ScriptContext context)
			throws ScriptException {
		return script.eval(context);
	}

	protected String getExtenstion(String ext) {
		int n = ext.lastIndexOf('.');
		return ext.substring(n + 1);
	}

	protected Object execute(Object script) throws ScriptException {
		ScriptContext context = engine.getContext();
		Writer error = new OutputStreamWriter(System.err);
		context.setErrorWriter(error);
		context.setWriter(writer);

		Object result;
		try {
			if (script instanceof CompiledScript) {
				result = exec((CompiledScript) script, context);
			} else {
				result = interpret(script.toString(), context);
			}
		} finally {
			try {
				error.flush();
				writer.flush();
			} catch (IOException e) { }

		}
		return result;
	}

	public ScriptShell getScriptEnvironment(String ext) {
		return new ScriptShell(applicationContext, ext);
	}

	@SuppressWarnings("rawtypes")
	static class List extends ArrayList {
		private static final long serialVersionUID = 2686049123509847387L;
	}

	@SuppressWarnings("rawtypes")
	static class Map extends LinkedHashMap {
		private static final long serialVersionUID = 2686049123509847387L;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public Writer getWriter() {
		return writer;
	}

	public void setWriter(Writer w) {
		writer = w;
	}

}
