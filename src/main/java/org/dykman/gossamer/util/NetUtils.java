package org.dykman.gossamer.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;

public class NetUtils
{

	public static boolean dumpURL(URL in, File out) {
		return dumpURL(in, out,false);
	}

	public static boolean dumpURL(URL in, File out, boolean replace) {
		
		boolean success = false;
		try {
			if(replace || ! out.exists()) {
				InputStream inputStream = in.openStream();
				OutputStream outputStream = new FileOutputStream(out);
				byte[] buff = new byte[4096];
				int n;
				while((n = inputStream.read(buff)) >=0) {
					outputStream.write(buff, 0, n);
				}
				
				inputStream.close();
				outputStream.close();
				success = true;
			}
		} catch(Exception e) {
			success = false;
			System.err.println("error in dump URL: " + e.getMessage()+ " - " + in.toExternalForm());
			e.printStackTrace();
		}
		return success;
	}

	public static void dumpStream(InputStream in, OutputStream out)
	{
		byte[] bb = new byte[4096];
		int n;
		try
		{
			while((n = in.read(bb)) > -1)
			{
				out.write(bb,0,n);
			}
			out.flush();
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public static void showExires(HttpServletResponse response,String msg)
	{
		boolean b = response.containsHeader("Expires");
		System.out.println(msg + ": expires header " + (b ? " is defined" : " is NOT defined"));
	}
}
