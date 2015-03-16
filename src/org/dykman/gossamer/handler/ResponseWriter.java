package org.dykman.gossamer.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public interface ResponseWriter
{
	public void setMimeType(String type);
	public void setHeader(String name,String value);
	public void setStatus(int status);
	public int getStatus();
	
	public void print(String s) throws IOException;
	public void println(String s) throws IOException;
	public void write(byte[] bb)  throws IOException;
	public OutputStream getOutputStream() throws IOException;
	public Writer getWriter() throws IOException;
	public void flush() throws IOException;
	public void destroy();
}
