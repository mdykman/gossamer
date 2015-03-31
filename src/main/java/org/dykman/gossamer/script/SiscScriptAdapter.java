package org.dykman.gossamer.script;

import java.math.BigInteger;

import sisc.data.EmptyList;
import sisc.data.Nothing;
import sisc.data.Pair;
import sisc.data.Quantity;
import sisc.data.SchemeString;
import sisc.data.Value;
import sisc.data.Values;

public class SiscScriptAdapter extends SimpleScriptAdapter
{
	@SuppressWarnings("unchecked")
    public Object toJava(Object o)
	{
		Object result = null;
		if(o instanceof Pair) {
			Pair pair = (Pair) o;
			List l = new List();
			l.add(toJava(pair.car()));
			Value oo = pair.cdr();
			if(oo != null && ! oo.equals(EmptyList.EMPTYLIST))
			{
				Object rr = toJava(oo);
				if(rr instanceof List) {
					l.addAll((List)rr);
				}
				else {
System.out.println("Scheme Pair: i don't think this branch is used");					
					l.add(rr);
				}
			}
			result = l;
		}
		else if( o instanceof Values) {
			Object[] oo = ((Values)o).values;
			List l = new List();
			for(int i = 0;i < oo.length; ++i) {
				l.add(toJava(oo[i]));
			}
			result = l;
		}
		else if ( o instanceof Quantity) {
			Quantity q = (Quantity) o;
			if(q.de.equals(BigInteger.ONE)) {
				result = q.i;
			}
			else {
				result = q.decimal();
			}
		}
		else if ( o instanceof SchemeString) {
			result = ((SchemeString)o).asString();
		}
		else if ( o instanceof Nothing) {
			result = null;
		}
		else if ( o instanceof Value) {
System.out.println("   scheme value " + o.getClass().getName());
			result = ((Value)o).toString();
		}
		else {
System.out.println("      scheme unknown " + o.getClass().getName());
			result = o;
		}
		return result;
	}
}
