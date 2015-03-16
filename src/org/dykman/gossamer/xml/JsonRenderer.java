package org.dykman.gossamer.xml;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Map;

import org.dykman.gossamer.core.GossamerException;
import org.dykman.gossamer.core.Renderer;

public class JsonRenderer implements Renderer
{
	String	   encoding = "UTF-8";
	DateFormat dateFormat;

	public void setEncoding(String encoding)
	{
		this.encoding = encoding;
	}
	public void setView(String s) {}

	public void render(Object o, Writer out) throws Exception {
			render(o,out,false);
	}

	public void render(Object o, Writer writer, boolean indent) throws Exception
	{
		try
		{
			printObject(writer, o);
			writer.write("\r\n");
			writer.flush();
		} 
		catch (Exception e)
		{
			throw new GossamerException(e);
		}

	}

	public void printObject(Writer writer, Object o) throws Exception
	{
		listProperties(writer, o,0);
	}

	public String getContentType()
    {
    	return "text/plain";
    }
	private static void indent(Writer w, int n)
		throws IOException	{
		for(int i = 0; i < n; ++i) {
			w.write("  ");
		}
	}

    protected void listProperties(Writer writer, Object o,int depth)
	        throws Exception
	{
		if (o == null)
			return;
		Class<?> klass = o.getClass();

		if (o instanceof Map)
		{
			Map<?, ?> m = (Map<?, ?>) o;
			writer.write("{ ");
			boolean first = true;
			for (@SuppressWarnings("rawtypes") Map.Entry key : m.entrySet())
			{
				if (!first) {
//					writer.write(",");
					writer.write(",\n");
					indent(writer,depth+1);
				}
				writer.write("\"" + key.getKey().toString() + "\":");
				listProperties(writer, key.getValue(), depth+1);
				first = false;
			}
			writer.write("\n");
			indent(writer,depth);
			writer.write("}");
		} else if (o instanceof Collection)
		{
			Collection<?> m = (Collection<?>) o;
			writer.write("[");
			boolean first = true;
			for (Object key : m)
			{
				if (!first) {
//					writer.write(",");
					writer.write(",\n");
					indent(writer,depth+1);
				}
				listProperties(writer, key, depth+1);
				first = false;
			}
//			writer.write("\n");
//			indent(writer,depth);
			writer.write("]");
		} else if (klass.isArray())
		{
			Object[] m = (Object[]) o;
			writer.write("[");
			boolean first = true;
			for (Object key : m)
			{
				if (!first) {
//					writer.write(",");
					writer.write(",\n");
					indent(writer,depth+1);
				}
				listProperties(writer, key, depth+1);
				first = false;
			}
//			writer.write("\n");
//			indent(writer,depth);
			writer.write("]");
		} else if (o instanceof String) {
			String s = (String)o;
			writer.write('"' + encodeString(s) + '"');
		} else if (RendererUtils.isScalar(o, klass)) {
			writer.write(o.toString());
		} else if (o instanceof java.util.Date) {
			writer.write('"' + getDateFormat().format((java.util.Date) o) + '"');
		} else if (o instanceof java.util.Calendar) {
			java.util.Calendar cal = (java.util.Calendar) o;
			writer.write('"' + getDateFormat().format(cal.getTime()) + '"');
		} else {
			listObject(writer, o.getClass(), o,depth+1);
		}
	}

	protected String encodeString(String s) {
		String result = s;
		result = result.replaceAll("[\\\\]", "\\\\");
		result = result.replaceAll("[\"]", "\\'");
		result = result.replaceAll("[\\x13]", "\\r");
		result = result.replaceAll("[\\x10]", "\\n");
		
		return result ;
	}
	protected void listObject(Writer writer, Class<?> klass, Object o, int depth)
	throws Exception
	{
		writer.write("{ ");
		listFields(writer, klass, o,depth);
//		writer.write("\n");
//		indent(writer,depth);
		writer.write("}");
	}

	protected int listFields(Writer writer, Class<?> klass, Object o,int depth)
		throws Exception
	{
		if (klass == Object.class) return 0;
		int n = listFields(writer, klass.getSuperclass(), o,depth);

		Field[] fields = klass.getDeclaredFields();
		for (Field field : fields) {
			if((field.getModifiers() & Modifier.TRANSIENT)  == 0)
			{
				Method m = RendererUtils.getAccessor(klass, field);
				if(m != null && (m.getModifiers() & Modifier.PUBLIC) != 0) {
					if(n++ > 0) {
//						writer.write(",");
						writer.write(",\n");
						indent(writer,depth+1);
					}
					
					writer.write("\"" + field.getName() + "\":");
					listProperties(writer, m.invoke(o, new Object[] {}),depth+1);
				}
			}
		}
		return n;
	}

	protected DateFormat getDateFormat()
	{
		if (dateFormat == null)
		{
			dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
		}
		return dateFormat;
	}
}
