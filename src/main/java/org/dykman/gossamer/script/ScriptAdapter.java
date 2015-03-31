package org.dykman.gossamer.script;

import java.io.Reader;
import java.io.Writer;
import java.util.Map;

import javax.script.CompiledScript;
import javax.script.ScriptException;

public interface ScriptAdapter
{
	public Object invokeScript(Object script)
		throws ScriptException;
	public void setScriptEngine(String ext);
	public void setGlobalBindings(Map<String,Object> bindings);
	public void setWriter(Writer w);
	public String getType();
	public CompiledScript compile(Reader reader)
		throws ScriptException;

	public boolean compileHint();
	public Object toJava(Object o);
//	public void setExtension(String extension);

}
