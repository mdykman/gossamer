package org.dykman.gossamer.core;

import java.io.Writer;

public interface Renderer
{
	public void setView(String view);
	public String getContentType();
	
	public void render(Object o, Writer out)
		throws Exception;
	public void render(Object o, Writer out, boolean indent)
		throws Exception;

}
