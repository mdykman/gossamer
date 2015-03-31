package org.dykman.gossamer.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.dykman.gossamer.core.GossamerException;


public class DocumentBuilderManager
{
	String implementation = null;
	DocumentBuilderFactory factory = null;
	
	protected void loadFactory() {
		if(factory == null) {
			if(implementation == null) {
				factory = DocumentBuilderFactory.newInstance();
			}
			else {
				factory = DocumentBuilderFactory.newInstance(implementation,null);
			}
		}
	}
	
	public DocumentBuilder createDocumentBuilder() {
		loadFactory();
		try {
			return factory.newDocumentBuilder();
		} catch(Exception e) {
			throw new GossamerException(e);
		}
	}

	public String getImplementation() {
		return implementation;
	}
	public void setImplementation(String implementation)
    {
    	this.implementation = implementation;
    }
}
