package org.dykman.gossamer.xml;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;

public class RendererUtils
{
	public static boolean isScalar(Object o)
	{
		return isScalar(o, o.getClass());
	}
	public static boolean isIterable(Object o)
	{
		return isScalar(o, o.getClass());
	}
	
	public static boolean isStringlike(Object o,Class<?> klass) {
		if(o instanceof StringBuffer 
			|| o instanceof StringBuilder
			|| o instanceof CharSequence) {
			return true;
		}
		return false;
	}
	public static boolean isScalar(Object o,Class<?> klass)
	{
		// TODO... could use some formatting for the float/double classes
		return (o instanceof String 
			|| o instanceof StringBuffer 
			|| o instanceof StringBuilder 
			|| klass == Integer.class || klass ==  Long.class 
			|| klass == Integer.TYPE || klass == Long.TYPE
			|| klass == Boolean.class || klass == Boolean.TYPE
			|| klass == Float.class || klass ==  Double.class 
			|| klass == Float.TYPE || klass == Double.TYPE
			|| klass == BigDecimal.class || klass == BigInteger.class);
	}
	
	public static Method getAccessor(Class<?> klass, Field field)
	{
		Method method = null;
		String name = field.getName();
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("get").append(name.substring(0, 1).toUpperCase()).append(name.substring(1));
		try {
			method = klass.getMethod(buffer.toString());
			//, new Class<?>[]{});
		}
		catch(NoSuchMethodException e) {
// it's handled, just return null here		
		}
		return method;
	}

}
