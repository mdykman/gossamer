package org.dykman.gossamer.script;


import java.util.ListIterator;

import javax.script.ScriptException;

import org.python.core.PyDictionary;
import org.python.core.PyObject;
import org.python.core.PyTuple;

public class JythonScriptAdapter extends SimpleScriptAdapter 
{
	@Override
	public Object invokeScript( Object script)
		throws ScriptException
    {
		System.setProperty("python.home", "/tmp");
//		engine.put("python.home","/tmp");
		super.invokeScript(script);
		Object pfd = getBindings().get("result");
		
		return toJava(pfd);
    }
	
	@SuppressWarnings("unchecked")
    public Object toJava(Object o)
	{
		Object result = null;
		if(o == null)return null;
		if(o instanceof PyDictionary) {
			java.util.Map<Object, Object> m = new Map();
			ListIterator<PyTuple> it = ((PyDictionary) o)
				.items().listIterator();
			while(it.hasNext()) {
				PyTuple pt = it.next();
				m.put(pt.get(0).toString(), toJava(pt.get(1)));
			}
			result = m;
		}
		else if(o instanceof PyTuple) {
			PyTuple pt = (PyTuple) o;
			int n = pt.__len__();
			List tup = new List();
			for(int i = 0; i < n; ++i) {
				tup.add(toJava(pt.get(i)));
			}
			result = tup;
		}
		else if(o instanceof PyObject) {
			result = o;
System.out.println(" WARNING: Jython need to define handler for " + o.getClass().getName());
		}
		else {
			result = o;
		}
		return result;

	}
}
