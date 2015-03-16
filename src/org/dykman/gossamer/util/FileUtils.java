package org.dykman.gossamer.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.dykman.gossamer.core.GossamerException;

public abstract class FileUtils
{
	public static String toHex(byte[] bb)
	{
		StringBuffer buffer = new StringBuffer();
		for(byte b : bb)
		{
			int n = 0xff & b;
			if(n < 16)  buffer.append('0');
			buffer.append(Integer.toHexString(n));
		}
		return buffer.toString();
	}
	public static String readFile(File f)
		throws IOException
	{
		StringBuffer bb = new StringBuffer();
		FileReader reader = new FileReader(f);
		char[] cc = new char[4096];
		int n;
		while((n = reader.read(cc)) > 0) {
			bb.append(cc,0,n);
		}
		reader.close();
		return bb.toString();
	}
	public static File tempFile(String s,String ext)
	{
		File f = null;
		try
		{
			f = File.createTempFile("gossamer_", ext);
			Writer w = new FileWriter(f);
			w.write(s);
			w.flush();
			w.close();
		}
		catch(Exception e)
		{
			throw new GossamerException(
				"failed to write temporary file " + e.getMessage());
		}
		return f;
	}

	public static void saveToFile(File f, String s)
	{
		try
		{
			Writer w = new FileWriter(f);
			w.write(s);
			w.flush();
			w.close();
		}
		catch(Exception e)
		{
			throw new GossamerException("failed top write " 
					+ f.getAbsolutePath() + " " + e.getMessage());
		}
	}
}
