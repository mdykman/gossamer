package org.dykman.gossamer.webapp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dykman.gossamer.core.GossamerException;
import org.dykman.gossamer.handler.ResponseWriter;

public class WebResponseWriter implements ResponseWriter
{

	HttpServletRequest request;
	HttpServletResponse response;

	boolean binary = false;
	boolean text = false;
	int status = 0;

	public void setStatus(int s) {
		status = s;
		response.setStatus(s);
	}
	public int getStatus() {
		return status;
	}
	public WebResponseWriter(HttpServletRequest request,
			HttpServletResponse response)
	{
		this.request = request;
		this.response = response;
	}

	public void flush()
		throws IOException
	{
		if(binary) {
			response.getOutputStream().flush();
		}
		else if(text) {
			response.getWriter().flush();
		}
	}

	public OutputStream getOutputStream()
		throws IOException
	{
		if(text) throw new GossamerException("cant switch output");
		binary = true;
		return response.getOutputStream();
	}

	public Writer getXMLWriter() 
	throws IOException
	{
		response.setHeader("Content-Type","text/xml");
		return getWriter();
	}

	public Writer getWriter()
		throws IOException
	{
		if(binary) throw new GossamerException("cant switch output");
		text = true;
		return response.getWriter();
	}

	public void print(String s)
		throws IOException
	{
		getWriter().write(s);
	}

	public void println(String s)
		throws IOException {
		print(s + "\n");
	}

	public void write(byte[] bb)
		throws IOException {
		getOutputStream().write(bb);
	}

	public void setHeader(String name, String value) {
		response.setHeader(name, value);
	}

	public void setMimeType(String type) {
		response.setContentType(type);
	}

	public void serveFile(String path,String type) {
		setMimeType(type);
		try
		{
			

			OutputStream out = getOutputStream();
			byte[] bb = new byte[4096];
			int n;
			FileInputStream in = new FileInputStream(path);
			while((n = in.read(bb)) > 0) {
				out.write(bb,0,n);
			}
			in.close();
		}
		catch(IOException e)
		{
			System.out.println("WARNING: error while writing " 
				+ e.getMessage());
		}
	}
	public void destroy() {
		try {
			flush();
		}
		catch(IOException e) {
			System.out.println("WARNING: error onflush output");
		}
	}

}
