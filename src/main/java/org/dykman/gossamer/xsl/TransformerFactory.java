package org.dykman.gossamer.xsl;

import org.dykman.gossamer.webapp.Transformer;

public interface TransformerFactory
{
	public Transformer createTransformer(String userAgent, String contentType, String view);
}
