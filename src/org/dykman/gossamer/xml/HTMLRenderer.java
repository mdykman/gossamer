package org.dykman.gossamer.xml;

import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.dykman.gossamer.core.GossamerException;
import org.dykman.gossamer.core.Renderer;
import org.dykman.gossamer.xsl.XslTransformerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;

public class HTMLRenderer implements Renderer, ApplicationContextAware
{
	private String	   baseTag;
	String view;
	ApplicationContext	applicationContext;

	OutputHandler	   outputHandler;

	public void setBaseTag(String baseTag)
	{
		this.baseTag = baseTag;
	}

	public void render(Object o, Writer  out) throws Exception {
		render(o,out,false);
	}
	public void render(Object o, Writer out, boolean ind) throws Exception
	{
		if (o instanceof Document) {
			writeDocument((Document) o, out);
		} else if (o instanceof Node) {
			DocumentBuilderManager manager = (DocumentBuilderManager) applicationContext
			        .getBean("DocumentBuilderManager");

			DocumentBuilder builder = manager.createDocumentBuilder();
			Document document = builder.newDocument();
			Node pp = document;
			if(o instanceof DocumentFragment) {
				pp  = document.createElement(baseTag);
				document.appendChild(pp);
			}
			pp.appendChild((Node)o);
			writeDocument(document, out);
		} else {
			OutputHandler outputHandler = new StreamContentHandler();
			outputHandler.setWriter(out);
			XmlWriter xmlWriter = new XmlWriter(outputHandler, baseTag);
			xmlWriter.write(o);
		}
	}
	
	public String getContentType() {
    	return "text/html";
    }
	
	protected void writeDocument(Document document, Writer out)
	{
		XslTransformerFactory factory = (XslTransformerFactory) applicationContext
		        .getBean("xslTtransformerFactory");
		Transformer transformer = factory.getTransformer(null);
		Source source = new DOMSource(document);
		Result result = new StreamResult(out);
		try
		{
			transformer.transform(source, result);
		} catch (Exception e)
		{
			throw new GossamerException(e);
		}

	}

	public void setApplicationContext(ApplicationContext applicationContext)
	{
		this.applicationContext = applicationContext;
	}

	public String getView()
    {
    	return view;
    }

	public void setView(String view)
    {
    	this.view = view;
    }
}
