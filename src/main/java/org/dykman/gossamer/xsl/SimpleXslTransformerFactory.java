package org.dykman.gossamer.xsl;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

public class SimpleXslTransformerFactory implements XslTransformerFactory
{
	private javax.xml.transform.TransformerFactory factory;
	private Map<String, Templates> cache = new HashMap<String, Templates>();
	private boolean cacheTemplates = true;
	private String forcedDomain = null;
	private String implementation = null;
	
	public SimpleXslTransformerFactory() {
	}

	public void setImplementation(String impl) {
		implementation = impl;
	}

	public void loadFactory() {
		if(factory == null) {
			if(implementation == null) {
				factory = TransformerFactory.newInstance();
			} else {
				factory = TransformerFactory.newInstance(
					implementation, null);
			}
		}
		
	}
	public void setCacheTemplates(boolean b)
	{
		this.cacheTemplates = b;
	}
	
	public Transformer getTransformer(String url)
	{
		try
		{
			loadFactory();
			if(url == null) {
				return factory.newTransformer();
			} else {
				Templates templates = null;
				if(cacheTemplates) templates = cache.get(url);
				if(templates == null)
				{
					// see if it's on the file system?
					String nu = url;
					if(forcedDomain != null) {
						nu = translateToLocal(url);
					}

					// System.out.println("SimpleXslTransformer: loading from url: " + nu);					

					javax.xml.transform.Source src = new StreamSource(nu);
					templates = factory.newTemplates(src);
					if(cacheTemplates) cache.put(url, templates);
				}
				return templates.newTransformer();
			}
			
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	protected String translateToLocal(String url) {
		try {
			URL uu = new URL(url);
			URL nu = new URL(uu.getProtocol(),forcedDomain,uu.getPort(),uu.getFile());
			
			return nu.toExternalForm();
		} catch(Exception e ) {
			throw new RuntimeException(e);
		}
	}

	public String getForcedDomain()
    {
    	return forcedDomain;
    }

	public void setForcedDomain(String forcedDomain)
    {
		if(forcedDomain != null && forcedDomain.trim().length() > 0) {
			this.forcedDomain = forcedDomain;
		}
    }

}
