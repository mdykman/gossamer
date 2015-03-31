package org.dykman.gossamer.util;
//ddpackage com.thejobroute.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Hash
{
	private static final String md5Extra = "Tracy was Here....Deal";
	private String replace( String s, String f, String r )
	{
	   if (s == null)  return s;
	   if (f == null)  return s;
	   if (r == null)  r = "";

	   int index01 = s.indexOf( f );
	   while (index01 != -1)
	   {
	      s = s.substring(0,index01) + r + s.substring(index01+f.length());
	      index01 += r.length();
	      index01 = s.indexOf( f, index01 );
	   }
	   return s;
	}
	public void addSlashes(String str)
	{
		if(str==null) str = "";
		
		str = replace(str, "\\", "\\\\");
		str = replace(str, "\'", "\\\'");
		str = replace(str, "\"", "\\\"");

	}
	public String md5(File file) {
		return md5(file,md5Extra);
	}
	public String md5(File file,String salt)
	{
		byte[] b = new byte[4096];
		byte[] result = null;
		try
		{
			FileInputStream fileInputStream = new FileInputStream(file);
			MessageDigest md = MessageDigest.getInstance("MD5");
			int n =0;
			md.update(salt.getBytes());
			while((n = fileInputStream.read(b, 0, 4096)) != -1)
			{
				if(n > 0)
				{
					md.update(b, 0, n);
				}
			}
			result = md.digest();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		catch(NoSuchAlgorithmException e)
		{
			System.err.println("what the hell?  you don't support MD5!!.. you piece of crap");
		}
		String mdsStr = md5(toString(result));
		addSlashes(mdsStr);
		return mdsStr;
	}
	public String md5(String s) {
		return md5(s,md5Extra);
	}
	public String md5(String s,String salt)
	{
		s = s + salt;
		byte[] result = null;
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(s.getBytes());

			result = md.digest();
		}
		catch(NoSuchAlgorithmException e)
		{
			System.err.println("what the hell?  you don't support MD5!!.. you piece of crap");
		}
		String mdsStr = toString(result);
		addSlashes(mdsStr);
		return mdsStr;
	}
	public String sha(String s)
	{
		byte[] result = null;
		try
		{
			MessageDigest md = MessageDigest.getInstance("SHA");
			md.update(s.getBytes());

			result = md.digest();
		}
		catch(NoSuchAlgorithmException e)
		{
			System.err.println("what the hell?  you don't support SHA!!.. you piece of crap");
		}
		String str = new String(result);
		addSlashes(str);
		return str;
	}
	public String toString(byte[] bytes)
    {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < bytes.length; ++i)
            {
                    int n = bytes[i];
                    if (n < 0)
                    {
                            n = 127 - n;
                    }
                    if (n < 16)
                    {
                            buffer.append("0");
                    }
                    buffer.append(Integer.toHexString(n));
            }
            return buffer.toString();
    }
}
