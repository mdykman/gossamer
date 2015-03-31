package org.dykman.gossamer.script;


import javax.script.ScriptEngineFactory;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.NativeObject;

import com.sun.phobos.script.javascript.RhinoScriptEngineFactory;

public class RhinoScriptAdapter extends SimpleScriptAdapter 
{
	@Override
	public void setScriptEngine(String ext) {
		ScriptEngineFactory factory = 
			new RhinoScriptEngineFactory();
		engine = factory.getScriptEngine();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object toJava(Object o) {
		Object result = null;
		if(o instanceof NativeObject)
		{
			Map m = new Map();
			NativeObject no = (NativeObject)o;
			Object[] oo = no.getAllIds();
			for(Object k : oo) {
				m.put(k.toString(), toJava(no.get(k.toString(), null)));
			}
			result = m;
		}
		else if(o instanceof NativeJavaObject)
		{
			result = ((NativeJavaObject)o).unwrap();
		}
		else if(o != null)
		{
			System.out.println("WARNING: RHINO trace warning: unconverted " 
					+ o.getClass().getName());			
			result = o;
		}
		return result;
	}
}
