package org.dykman.gossamer.xml;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class XmlWriter
{
	public static final String LIST_ITEM = "item"; 
	OutputHandler outputHandler;

	DateFormat dateFormat = null;
	String base;
	public XmlWriter (OutputHandler outputHandler, String base)
	{
		this.outputHandler = outputHandler;
		this.base  = base;
	}

	public void write(Object o)
		throws Exception
	{
		outputHandler.startDocument();
		outputHandler.startElement(null, base, null, null);
		listProperties(o);
		outputHandler.endElement(null, base, null);
		outputHandler.endDocument();
	}
	
	protected void iterateSequence(Iterator<?> it)
		throws Exception
	{
		Object key;
		while(it.hasNext()) {
			key = it.next();
			String label = getListLabel(key, key.getClass());
			
			outputHandler.startElement(null, label, null, null);
			listProperties(key);
			outputHandler.endElement(null, label, null);
		}
	}

	protected String getListLabel(Object key,Class<?> type) {
		String result = RendererUtils.isScalar(key,type) 
			|| type.isArray() || key instanceof Collection<?>
		    ? LIST_ITEM : null;
		if(result == null && key instanceof Map) {
			result = "Map";
		}
		if(result == null) {
			result = type.getSimpleName();
		}
		return result;
	}

	
	protected void listProperties(Object o)
		throws Exception
	{
		if(o == null) return;
		Class<?> klass = o.getClass();
		
		if(o instanceof Map) {
			Map <?,?>m = (Map<?,?>)o;
			for(@SuppressWarnings("rawtypes") Map.Entry key : m.entrySet()) {
				String label = key.getKey().toString();
				outputHandler.startElement(null, label,null,  null);
				listProperties(key.getValue());
				outputHandler.endElement(null, label, null);
			}
		}
		else if(o instanceof Collection) {
			Collection<?> m = (Collection<?>)o;
			iterateSequence(m.iterator());
		}
		else if(klass.isArray()) {
			Object[] m = (Object[])o;
			for(Object key : m) {
				Class<?> type= key.getClass();
				String label = getListLabel(key, type);
				outputHandler.startElement(null, label, null,  null);
				listProperties(key);
				outputHandler.endElement(null, label, null);
			}
		}
		else if(o instanceof String) {
			characters(o.toString());
		}
		else if(RendererUtils.isScalar(o,klass)) {
			characters(o.toString());
		}
		else if(o instanceof java.util.Date) {
			characters(getDateFormat().format((java.util.Date) o));
		}
		else if(o instanceof java.util.Calendar) {
			java.util.Calendar cal = (java.util.Calendar) o;
			characters(getDateFormat().format(cal.getTime()));
		}
		else {
			listObjectProperties(o);
		}
	}

	protected DateFormat getDateFormat()
	{
		if(dateFormat == null)	{
			dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
		}
		return dateFormat;
	}
	protected void characters(String s)
		throws Exception
	{
		outputHandler.characters(s.toCharArray(), 0, s.length());
	}


	protected void listObjectProperties(Object o)
		throws Exception
	{
		listFields(o.getClass(),o);
	}
	
	protected void listFields(Class<?> klass,Object o)
		throws Exception
	{
		if(klass == Object.class) {
			return;
		} else {
			listFields(klass.getSuperclass(),o);
		}

		Field[] fields = klass.getDeclaredFields();
		for(Field field : fields) {
			if((field.getModifiers() & Modifier.TRANSIENT) == 0)
			{
				Method m = RendererUtils.getAccessor(klass,field);
				if(m != null && (m.getModifiers() & Modifier.PUBLIC) != 0) {
					outputHandler.startElement(null, field.getName(), null, null);
					listProperties(m.invoke(o, new Object[] {}));
					outputHandler.endElement(null, field.getName(), null);
				}
			}
		}
	}

}
