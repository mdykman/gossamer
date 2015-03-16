package org.dykman.gossamer.script;

import java.util.Iterator;

import com.caucho.quercus.env.ArrayValue;
import com.caucho.quercus.env.JavaListAdapter;
import com.caucho.quercus.env.JavaMapAdapter;
import com.caucho.quercus.env.ObjectExtJavaValue;
import com.caucho.quercus.env.StringValue;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.env.ValueType;

public class QuercusScriptAdapter extends SimpleScriptAdapter
{
	public Object toJava(Object o)
	{
		Object result = null;
		if(o instanceof ObjectExtJavaValue) {
			result = ((ObjectExtJavaValue)o).toJavaObject();
		}
		else if(o instanceof JavaMapAdapter) {
			result = ((JavaMapAdapter)o).toJavaMap(null, Object.class);
		}
		else if(o instanceof JavaListAdapter) {
			result = ((JavaListAdapter)o).toJavaList(null, Object.class);
		}
		else if(o instanceof Value) {
			result = convObject(((Value)o));
		}
		else if(o == null)
		{
			result = null;
		}
		else
		{
			result = o;
//System.out.println("   toJava: passed " + o.getClass().getName());
		}
		return result;
	}
		
	@SuppressWarnings("unchecked")
    protected Object convPhpObject(Value vv) {
		java.util.Map<Object, Object> m = new Map();
		Iterator<Value> it = vv.getKeyIterator(null);
		while(it.hasNext())
		{
			Value k = it.next();
			Value v = vv.getField(null,(StringValue)k);
			m.put(toJava(k),toJava(v));
		}
		return m;
	}
	
	@SuppressWarnings("unchecked")
    protected Object convObject(Value o)
	{
		Object result = null;
		ValueType vt = o.getValueType();
		
		if( vt.equals(ValueType.DOUBLE_CMP) 
				|| vt.equals(ValueType.DOUBLE) 
				) {
			result = o.toDouble();
		}
		else if( vt.equals(ValueType.LONG) 
				|| vt.equals(ValueType.LONG_ADD) 
				|| vt.equals(ValueType.LONG_EQ)) {
			result=o.toJavaLong();
		}
		else if(vt.equals(ValueType.ARRAY)) {
			ArrayValue av = (ArrayValue) o;
			Value[] vv = av.getKeyArray(null);
			Map m = new Map();
			List l = new List();
			boolean hasKeys = false;
			for(Value k : vv) {
//System.out.println("  array: " + k.toJavaString());				
				if(!k.isNumeric())
				{
					hasKeys = true;
					m.put(toJava(k),toJava(av.get(k)));
				}
				else
				{
					l.add(toJava(av.get(k)));
				}
			}
			result = hasKeys ? m : l;
		}
		else if(vt.equals(ValueType.OBJECT)) {
			Map rr = new Map();
			rr.putAll(o.toJavaMap(null, Object.class));
			result = rr;
		}
		else if(vt.equals(ValueType.STRING)) {
			result = o.toJavaObject();
		}
		else if(vt.equals(ValueType.BOOLEAN)) {
			result = o.toJavaBoolean();
		}
		else if(vt.equals(ValueType.NULL)) {
			result = o.toJavaBoolean();
		}
		else if(vt.equals(ValueType.VALUE)) {
			if(o.isObject()) {
				result = convPhpObject(o);
			} else {
System.out.println("convobject: still looking: " + o.getClassName() + " " + o.getClass().getName());				
			}
		}
		return result;
	}
}
