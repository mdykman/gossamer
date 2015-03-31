package org.dykman.gossamer.xml;

/**
 * @author micheel dykman
 * my own little nasty hack to remove the one xerces dependancy this package held
 * it is functional but it certainly could be better.
 */
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class StreamContentHandler implements OutputHandler
{
	private String	       encoding;
	private BufferedWriter	writer;
	private String spaces = 
		"                                                                            ";
	public StreamContentHandler()
	{
	}
	public void setWriter(Writer w) {
		writer = new BufferedWriter(w);
	}

	public void characters(char[] ch, int start, int length)
	        throws SAXException
	{
		try
		{
			writer.write(ch, start, length);
		} 
		catch (java.io.IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void startDocument() throws SAXException
	{
		try
		{
			writer.write("<?xml version=\"1.0\" encoding=\"" + encoding
			        + "\" ?>");
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void endDocument() throws SAXException
	{
		try
		{
			writer.flush();
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void startElement(String uri, String localName, String name,
	        Attributes atts) throws SAXException
	{
		// margin(++indent);
		try
		{
			writer.newLine();
			writer.write("<" + localName);
			if (atts != null)
			{
				int n = atts.getLength();
				for (int i = 0; i < n; ++i)
				{
					writer.write(" " + atts.getLocalName(i));
					// I could be escaping attribute chars, but I"m not using
					// attributes in this model yet..
					writer.write("=\"" + atts.getValue(i) + "\"");
				}
			}
			writer.write(">");
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void endElement(String uri, String localName, String name)
		throws SAXException
	{
		try
		{
			writer.write("</" + localName + ">");
			writer.flush();
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void margin(int n)
	{
		try
		{
			writer.write(spaces.substring(0,n));
		} 
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void processingInstruction(String target, String data)
	        throws SAXException
	{
		System.out.println("processing instruction");
	}

	public void setDocumentLocator(Locator locator)
	{
		System.out.println("DID NOT EXPECT THIS");
	}

	public void skippedEntity(String name) throws SAXException
	{
		System.out.println("DID NOT EXPECT THIS");
	}

	public void startPrefixMapping(String prefix, String uri)
	        throws SAXException
	{
		System.out.println("DID NOT EXPECT THIS");
	}

	public void endPrefixMapping(String prefix) throws SAXException
	{
		System.out.println("DID NOT EXPECT THIS");
	}

	public void ignorableWhitespace(char[] ch, int start, int length)
	        throws SAXException
	{
		System.out.println("DID NOT EXPECT THIS");
	}
}
