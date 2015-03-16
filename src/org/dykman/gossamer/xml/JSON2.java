package org.dykman.gossamer.xml;

import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Map;

import org.dykman.gossamer.core.GossamerException;
import org.dykman.gossamer.core.Renderer;
import org.dykman.gossamer.device.ClientDeviceProfile;
import org.json.JSONWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class JSON2 implements Renderer
{
	String	   encoding = "UTF-8";
	DateFormat dateFormat;
	private boolean indent = false;
	ClientDeviceProfile device;
	
	public void setEncoding(String encoding)
	{
		this.encoding = encoding;
	}
	
	public void setDevice(ClientDeviceProfile device) {
		this.device = device;
	}

	public void setView(String s) {}
	public void render(Object o, Writer w) 
		throws Exception {
		render(o,w,false);
	}
	public void render(Object o, Writer w, boolean ind) 
		throws Exception
	{
		try {
			JSONWriter jw = new JSONWriter(w);
			boolean wrap = RendererUtils.isScalar(o);
			if(wrap) {
				jw.array();
			}
			listProperties(jw, o,0,ind || indent);
			if(wrap) {
				jw.endArray();
			}
			w.flush();
		} 
		catch (Exception e) {
			throw new GossamerException(e);
		}
	}

    protected void listProperties(JSONWriter writer, Object o,int depth,boolean ind)
	        throws Exception {
		if (o == null) { 
			writer.value(null);
			return;
		}
		Class<?> klass = o.getClass();

		if (o instanceof Map) {
			Map<?, ?> m = (Map<?, ?>) o;
			writer.object();
			for (Map.Entry<?, ?> key : m.entrySet()) {
				writer.key(key.getKey().toString());
				listProperties(writer, key.getValue(), depth+1,ind);
			}
			writer.endObject();
		} 
		else if (o instanceof Collection) {
			Collection<?> m = (Collection<?>) o;
			writer.array();
			for (Object key : m)
				listProperties(writer, key, depth+1,ind);
			writer.endArray();
		} 
		else if (klass.isArray()) {
			int length = Array.getLength(o);
			@SuppressWarnings("unused")
			Object[] m = (Object[]) o;
			writer.array();
			for(int i = 0; i < length; ++i) {
				listProperties(writer, Array.get(o, i), depth+1,ind);
			}
			writer.endArray();
		} else if (o instanceof java.util.Date) {
			writer.value(getDateFormat().format((java.util.Date) o));
		} else if (o instanceof java.util.Calendar) {
			java.util.Calendar cal = (java.util.Calendar) o;
			writer.value(getDateFormat().format(cal.getTime()));
		} else if (o instanceof Document) {
			listProperties(writer,(Document)((Document) o).getDocumentElement(),depth,ind);
		} else if (o instanceof Text ) {
			Text ee = (Text) o;
			writer.value(ee.getTextContent());
		} else if (o instanceof Element) {
			writer.array();

			Element ee = (Element) o;
			writer.value(ee.getNodeName());
			
			writer.object();
			NamedNodeMap nm = ee.getAttributes();
			int n = nm.getLength();
			for(int i = 0; i > n; ++i) {
				Node node = nm.item(i);
				writer.key(node.getNodeName()).value(node.getNodeValue());
			}
			writer.endObject();
			
			writer.array();
			NodeList nl = ee.getChildNodes();
			n = nl.getLength();
			for(int i = 0; i < n; ++i) {
				listProperties(writer,nl.item(i),depth+1,ind);
			}
			writer.endArray(); // end child list
			
			writer.endArray(); // end element
		} else if (RendererUtils.isStringlike(o, klass)) {
			String s = o.toString();
			
			//TODO:: should I auto-fix strings with new lines in them?
//			s = s.replaceAll("[\r][\n]", " \\n ");
//			s = s.replaceAll("[\r]|[\n]", " \\n ");

			writer.value(s);
		} else if (RendererUtils.isScalar(o, klass)) {
			writer.value(o);
		} else { // treat as a pojo and hope for the best
			listObject(writer, o.getClass(), o,depth+1,ind);
		}
	}

	protected void listObject(JSONWriter writer, Class<?> klass, Object o, int depth, boolean ind)
		throws Exception {
		writer.object();
		listFields(writer, klass, o,depth,ind);
		writer.endObject();
	}

	protected void listFields(JSONWriter writer, Class<?> klass, Object o,int depth, boolean ind)
		throws Exception
	{
		if (klass == Object.class) return;
		listFields(writer, klass.getSuperclass(), o,depth,ind);

		Field[] fields = klass.getDeclaredFields();
		for (Field field : fields) {
			if((field.getModifiers() & Modifier.TRANSIENT)  == 0) {
				Method m = RendererUtils.getAccessor(klass, field);
				if(m != null && (m.getModifiers() & Modifier.PUBLIC) != 0) {
					writer.key(field.getName());
					listProperties(writer, m.invoke(o, new Object[] {}),depth+1,ind);
				}
			}
		}
	}

	protected DateFormat getDateFormat() {
		if (dateFormat == null) {
			dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
		}
		return dateFormat;
	}

	public String getEncoding() {
    	return encoding;
    }

	public void setDateFormat(DateFormat dateFormat) {
    	this.dateFormat = dateFormat;
    }
	
	public void setIndent(boolean b) {
		indent = b;
	}

	public String getContentType()
    {
    	return device.getAttribute("json_mime_type");
    }

}
