package org.dykman.gossamer.device;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public interface OutputStreamDevice
{
	public void setContentType(String type);
	public void setEncoding(String encoding);
	
	public OutputStream getOutputStream(ServletRequest req, ServletResponse out)
		throws IOException;

}
