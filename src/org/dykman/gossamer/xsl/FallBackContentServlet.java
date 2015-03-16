package org.dykman.gossamer.xsl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

public class FallBackContentServlet extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5271918923746848799L;

	File base;
	
	Map<String, ArrayList<String>> fallbacks = new 	HashMap<String, ArrayList<String>>();
	Map<String,File> translations = new HashMap<String, File>();
	@Override
	public void init(ServletConfig config) 
		throws ServletException {
		String s= config.getInitParameter("fileBase");
		base = new File(s);
		s = config.getInitParameter("fallback");
		try {
			config(s);
		} catch(IOException e) {
			throw new ServletException(e);
		}
	}

	@Override
	public void service(ServletRequest request, ServletResponse response)
		throws ServletException
	{
		HttpServletRequest req = (HttpServletRequest) request;
//		HttpServletResponse res = (HttpServletResponse) response;
		String pathInfo = req.getPathInfo().substring(1);
		
		File f = translations.get(pathInfo);
		if(f == null) {
			
		}
		if(f != null) {
			try {
				serve(f,response);
			} catch(IOException e) {
				throw new ServletException(e);
			}
		}
	}
	
	protected void serve(File f,ServletResponse r) 
		throws IOException {
		OutputStream os = r.getOutputStream();
		InputStream in = new FileInputStream(f);
		byte[] buff = new byte[4096];
		int n;
		while((n = in.read(buff)) != -1) {
			os.write(buff,0,n);
		}
		in.close();
		os.flush();
	}
	protected void config(String conf) 
		throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(conf));
		String line;
		while((line = reader.readLine()) != null) {
			configChain(line);
		}
		reader.close();
	}
	protected void configChain(String line) {
		String[]sp = line.split("[ \\t]+");
		if(sp.length > 1) {
			ArrayList<String> al = new ArrayList<String>(sp.length-1);
			for(int i = 1; i < sp.length; ++i) {
				al.add(sp[i].trim());
			}
			fallbacks.put(sp[0].trim(), al);
		}
	}
}
