package org.dykman.gossamer.util;

import java.io.BufferedReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ArrayUtils
{
	public static String [] fromStringBlock(String s) {
		BufferedReader reader = new BufferedReader(new StringReader(s));
		String line;
		
		List<String> list = new ArrayList<String>();
		
		try
		{
			while((line = reader.readLine()) != null)
			{
				String n= line.trim();
				if(n.length() > 0)
				{
					list.add(n);
				}
			}
		}
		catch(Exception e)
		{
			System.err.println("------------------------------------------------");
			e.printStackTrace();
			// IOException extremely unlikely in a string reader
			throw new RuntimeException("FOUL, also should not happen",e);
		}
		return list.toArray(new String[list.size()]);
	}
	
	public static Map<String,?> objectToMap(Object o)
		throws Exception
	{
		Map<String,Object> map = new LinkedHashMap<String, Object>();
		Class<?> klass = o.getClass();
		
		Field[] fields = klass.getDeclaredFields();
		for(Field f : fields)
		{
			Method m = getAccessor(klass,f);
			if(m != null)
			{
				map.put(f.getName(),m.invoke(o, new Object[] {}));
			}
		}
		return map;
	}
	
	public static Method getAccessor(Class<?> klass, Field field)
	{
		Method method = null;
		String name = field.getName();
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("get").append(name.substring(0, 1).toUpperCase()).append(name.substring(1));
		try {
			method = klass.getMethod(buffer.toString(), new Class<?>[]{});
		}
		catch(NoSuchMethodException e) {
// it's handled, just return null here		
		}
		return method;
	}
}
