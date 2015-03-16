package org.dykman.gossamer.script;

import sleep.runtime.Scalar;
import sleep.runtime.ScalarArray;
import sleep.runtime.ScalarHash;

public class SleepScriptAdapter extends SimpleScriptAdapter
{

	@SuppressWarnings("unchecked")
	@Override
	public Object toJava(Object o)
	{
		Object result = null;
		if(o instanceof ScalarHash)
		{
			ScalarHash h = (ScalarHash)o;
			ScalarArray kk = h.keys();
			int n = kk.size();
			Map m = new Map();
			for(int i = 0; i < n; ++i)
			{
				Scalar k = kk.getAt(i);
				Scalar v = h.getAt(k);
				m.put(toJava(k), toJava(v));
			}
			result = m;
		}
		else if(o instanceof ScalarArray)
		{
			ScalarArray h = (ScalarArray)o;
			List l = new List();
			int n = h.size();
			for(int i = 0; i < n; ++i)
			{
				l.add(toJava(h.getAt(i)));
			}
			result = l;
		}
//		else if(o instanceof ScalarType)
//		{
//			ScalarType h = (ScalarType)o;
//			result = h.objectValue();
//		}
		else if(o instanceof Scalar)
		{
			Scalar h = (Scalar)o;
			result = h.objectValue();
		}
		else if(o.getClass().getPackage().getName().startsWith("sleep"))
		{
System.out.println("WARNING: sleep failed to convert " 
	+ o.getClass().getName());			
			result = o;
		}
		else
		{
			result = o;
		}
		return result;
	}
}
