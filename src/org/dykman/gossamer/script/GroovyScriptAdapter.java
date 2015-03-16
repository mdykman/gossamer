package org.dykman.gossamer.script;

import javax.script.ScriptException;

import org.codehaus.groovy.jsr223.GroovyScriptEngineFactory;

/*
// is needed if uncommenting the invokeScript override
import javax.script.Bindings;
import javax.script.ScriptException;
import org.codehaus.groovy.reflection.ClassInfo;
*/

public class GroovyScriptAdapter extends SimpleScriptAdapter
{
//	private String[] additionalPathArray;
	private GroovyScriptEngineFactory factory;
	
	public GroovyScriptAdapter() {
		factory = new GroovyScriptEngineFactory();
		
	}
	@Override
	protected Object execute(Object script) 
		throws ScriptException {
	/*
		Thread thread = Thread.currentThread();
		ClassLoader cl = thread.getContextClassLoader();
		cl.

*/
		Object o = super.execute(script);
		return o;
	}
	@Override
	public void setScriptEngine(String ext) {
		engine = factory.getScriptEngine();
	}

	/*
	public Object invokeScript(ScriptEngine engine, Object script, Bindings bindings)
	throws ScriptException {
		Object ret = super.invokeScript(engine, script, bindings);

		ClassInfo.clearModifiedExpandos(); 

		//groovy.lang.GroovySystem.getMetaClassRegistry().removeMetaClass(org.codehaus.groovy.reflection.ClassInfo.getClassInfo(script.getClass()).getMetaClass(script).getClass());

		return ret;
	}
	*/
	
	public void setAdditionalPaths(String paths) {
//		java.util.List<String> list = new ArrayList<String>();
//		String[] bb = paths.split(paths);
//		for(int i = 0; i < bb.length; ++i) {
//			String s = bb[i].trim();
//			if(s.length() > 0) {
//				list.add(s);
//			}
//		}
//		additionalPathArray = list.toArray(new String[list.size()]);
	}
	
}
