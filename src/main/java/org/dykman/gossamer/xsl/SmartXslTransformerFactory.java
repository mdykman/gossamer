package org.dykman.gossamer.xsl;

import java.io.File;

import javax.xml.transform.Transformer;

public class SmartXslTransformerFactory extends SimpleXslTransformerFactory
{
	String base;
	File baseFile;
	
	public Transformer getTransformer(String url)
	{
		/*
		try {
			String file = new URL(url).getFile();
			File target = new File(baseFile,file);
			
		} catch(MalformedURLException e) {
			
		}
		*/
		return super.getTransformer(url);
	}
	
	public void setBase(String base) {
		this.base = base;
		this.baseFile = new File(base);
	}
}
