package org.dykman.gossamer.xsl;

import javax.xml.transform.Transformer;

public interface XslTransformerFactory
{

	public Transformer getTransformer(String url);
	
}
