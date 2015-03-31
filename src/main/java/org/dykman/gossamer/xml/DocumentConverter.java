package org.dykman.gossamer.xml;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.dykman.gossamer.core.GossamerException;
import org.dykman.gossamer.device.OutputDevice;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class DocumentConverter 
{
	private String	   encoding = "UTF-8";
	private DateFormat dateFormat = null;
	private String base = "result";
	private boolean indent = false;
	private OutputDevice device;
	private String view;
	private String contentType;
	boolean lastWasString = false;
	
	
	public DocumentConverter() {
		
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void render(Object o) throws Exception {
		render(o,false);
	}
	
	public Document render(Object o,  boolean in) throws Exception {
		try {
			if(o instanceof Document) return (Document)o;
			// TODO these could be shared resources, maybe
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			
			Element el;
			if(o instanceof Element) {
				el = (Element) document.importNode((Element) o, true);
			} else {
				el = document.createElement(base);
			}
			
			document.appendChild(el);
			// assemble DOM 
			listProperties(document,el, o,1,in || indent);
			return document;
		}
		catch (Exception e) {
			throw new GossamerException(e);
		}
	}

	protected static String getLabel(Object o) {
		if(o == null || RendererUtils.isScalar(o)) {
			return "Item";
		}
		if(o instanceof Map) {
			return "Map";
		}
		if(o instanceof Collection || o.getClass().isArray()) {
			return "List";
		}
		return o.getClass().getSimpleName();
	}

	protected void indent(Document document,Element element, int n, boolean ind) {
		if(ind) {
			StringBuilder builder = new StringBuilder("\n");
			for(int i = 0; i < n; ++i) {
				builder.append("  ");
			}
			Text t = document.createTextNode(builder.toString());
			element.appendChild(t);
		}
	}
	
    protected void listProperties(Document document,Element element, Object o,int depth, boolean ind)
	        throws Exception {
		if (o == null)
			return;
		Class<?> klass = o.getClass();

		if (o instanceof Node) {
			Node nn = (Node) o;
			int nt = nn.getNodeType();
			switch(nt) {
				case Node.DOCUMENT_NODE:
					element.appendChild(document.importNode(((Document)o).getDocumentElement(),true));
				break;
				case Node.ELEMENT_NODE:
				case Node.TEXT_NODE:
				case Node.COMMENT_NODE:
					element.appendChild(document.importNode((Node)o,true));
				break;
			}
		} else if (o instanceof Map) {
			Map<?, ?> m = (Map<?, ?>) o;
			for (@SuppressWarnings("rawtypes") Map.Entry key : m.entrySet()) {
				indent(document,element,depth,ind);
				Element el = document.createElement(key.getKey().toString());
				element.appendChild(el);
				listProperties(document,el, key.getValue(), depth+1,ind);
			}
			if(lastWasString == false && m.size() > 0) {
				indent(document,element,depth-1,ind);
			}
			lastWasString = false;
		} 
		else if (o instanceof Collection) {
			Collection<?> m = (Collection<?>) o;
			for (Object obj : m) {
				indent(document,element,depth,ind);
				String label = getLabel(obj);
				Element el = document.createElement(label);
				element.appendChild(el);
				listProperties(document,el, obj, depth+1,ind);
			}
			if(lastWasString == false && m.size() > 0) {
				indent(document,element,depth-1,ind);
			}
			lastWasString = false;
		}
		else if (klass.isArray()) {
			int length = Array.getLength(o);
			for(int i = 0; i < length; ++i) {
				Object obj = Array.get(o, i);
				indent(document,element,depth,ind);
				String label = getLabel(obj);
				Element el = document.createElement(label);
				element.appendChild(el);
				listProperties(document,el, obj, depth+1,ind);
			}
			if(lastWasString == false && length > 0) {
				indent(document,element,depth-1,ind);
			}
			lastWasString = false;
		} else if (o instanceof java.util.Date) {
//			indent(document,element,depth,ind);
			Text t = document.createTextNode(dateFormat.format((java.util.Date) o));
			element.appendChild(t);
			lastWasString = true;
		} else if (o instanceof java.util.Calendar) {
//			indent(document,element,depth,ind);
			java.util.Calendar cal = (java.util.Calendar) o;
			Text t = document.createTextNode(dateFormat.format(cal.getTime()));
			element.appendChild(t);
			lastWasString = true;
		} else if (RendererUtils.isStringlike(o, klass)) {
//			indent(document,element,depth,ind);
			Text t = document.createTextNode(o.toString());
			element.appendChild(t);
			lastWasString = true;
		} else if (RendererUtils.isScalar(o, klass)) {
//			indent(document,element,depth,ind);
			Text t = document.createTextNode(o.toString());
			element.appendChild(t);
			lastWasString = true;
		} else { // treat as a pojo
			listObject(document,element, o.getClass(), o,depth,ind);
			lastWasString = true;
		}
	}

	protected void listObject(Document document,Element element, Class<?> klass, Object o, int depth, boolean ind)
		throws Exception {
		listFields(document,element, klass, o,depth,ind);
	}

	protected void listFields(Document document,Element element, Class<?> klass, Object o,int depth, boolean ind)
		throws Exception
	{
		if (klass == Object.class) return;
		listFields(document,element, klass.getSuperclass(), o,depth,ind);

		Field[] fields = klass.getDeclaredFields();
		for (Field field : fields) {
			if((field.getModifiers() & Modifier.TRANSIENT) == 0) {
				Method m = RendererUtils.getAccessor(klass, field);
				if(m != null && (m.getModifiers() & Modifier.PUBLIC) != 0) {
					indent(document,element,depth,ind);
					Element el = document.createElement(field.getName());
					element.appendChild(el);
					listProperties(document,el, m.invoke(o, new Object[] {}),depth,ind);
				}
			}
		}
		if(lastWasString == false && fields.length > 0) {
			indent(document,element,depth-1,ind);
		}
		lastWasString = false;
	}

	protected DateFormat getDateFormat() {
		if (dateFormat == null) {
			dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
		}
		return dateFormat;
	}

	public String getEncoding()
    {
    	return encoding;
    }

	public void setDateFormat(DateFormat dateFormat)
    {
    	this.dateFormat = dateFormat;
    }

	public void setBaseTag(String base)
    {
    	this.base = base;
    }

	public OutputDevice getDevice()
    {
    	return device;
    }

	public void setDevice(OutputDevice device)
    {
    	this.device = device;
    }

	public String getView()
    {
    	return view;
    }

	public void setView(String view)
    {
    	this.view = view;
    }

	public String getBase()
    {
    	return base;
    }

	public void setBase(String base)
    {
    	this.base = base;
    }

	public boolean isIndent()
    {
    	return indent;
    }

	public void setIndent(boolean indent)
    {
    	this.indent = indent;
    }

	public String getContentType()
    {
    	return contentType;
    }

	public void setContentType(String contentType)
    {
    	this.contentType = contentType;
    }
}
