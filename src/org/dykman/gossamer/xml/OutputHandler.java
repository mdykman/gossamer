package org.dykman.gossamer.xml;

import java.io.Writer;

import org.xml.sax.ContentHandler;

public interface OutputHandler extends ContentHandler
{
	public void setWriter(Writer writer);
}
