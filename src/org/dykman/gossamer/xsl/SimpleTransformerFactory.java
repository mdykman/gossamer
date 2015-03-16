package org.dykman.gossamer.xsl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.dykman.gossamer.device.ClientDeviceProfile;
import org.dykman.gossamer.device.DeviceProfileFactory;
import org.dykman.gossamer.webapp.Transformer;

public class SimpleTransformerFactory implements TransformerFactory
{
	private DeviceProfileFactory deviceProfleFactory;
	private XslTransformerFactory xslTransformerFactory;
	private XslResolver xslResolver;
	private String xslAttribute;
	
	public void setXslAttribute(String xslAttribute)
    {
    	this.xslAttribute = xslAttribute;
    }

	public void setDeviceProfileFactory(DeviceProfileFactory deviceProfleFactory)
	{
		this.deviceProfleFactory = deviceProfleFactory;
	}

	public void setXslTransformerFactory(XslTransformerFactory xslTransformerFactory)
	{
		this.xslTransformerFactory = xslTransformerFactory;
	}
	public void setXslResolver(XslResolver xslResolver)
    {
    	this.xslResolver = xslResolver;
    }

	public Transformer createTransformer(String userAgent, String contentType, String view)
	{
		ClientDeviceProfile profile = deviceProfleFactory.getDeviceProfile(userAgent);
		String xslPath = xslResolver.resolveXslUrl(profile,view);
		boolean hasXsl = profile.getBoolean(xslAttribute);

		Transformer transformer = null;
		if(hasXsl)
		{
			transformer = new ClientXslTransformer(xslPath,contentType);
		}
		else
		{
			javax.xml.transform.Transformer tt = xslTransformerFactory.getTransformer(xslPath);
			transformer = new ServerXslTransformer(tt);
		}
		return transformer;
	}

	static class ClientXslTransformer implements Transformer
	{
		private String xslPath;
		String contentType;

		private static final int initScanLength = 1024;
		
		public ClientXslTransformer(String xslPath, String contentType)
		{
			this.xslPath = xslPath;
			this.contentType = contentType;
		}
		
		public String getContentType()
		{
			return contentType;
		}
		
		public void transform(byte[] in,OutputStream out)
		{
			try
			{
				StringBuffer styleTag = new StringBuffer();
				styleTag.append("<?xml-stylesheet href=\"").append(xslPath)
				.append("\" type=\"text/xsl\" ?>\n");
		
				int c = in.length > initScanLength ? initScanLength : in.length;
				String head = new String(in,0,c);

				int n = head.indexOf('>');
				if(head.charAt(n-1) == '?')
				{
					out.write(head.substring(0, n+1).getBytes());
					head = head.substring(n+1);
					out.write("\n".getBytes());
				}
				out.write(styleTag.toString().getBytes());
				out.write(head.getBytes());
				if(in.length > initScanLength)
				{
					out.write(in,c,in.length  - c);
				}
			}
			catch(IOException e)
			{
				throw new RuntimeException(e);
			}
		}
	}
	
	static class ServerXslTransformer implements Transformer
	{
		javax.xml.transform.Transformer transformer;
		public ServerXslTransformer(javax.xml.transform.Transformer transformer)
		{
			this.transformer = transformer;
		}
		
		public String getContentType()
		{
			return  "text/html";
		}
		
		public void transform(byte[] _in,OutputStream out)
		{
			
			Result result = new StreamResult(out);
			ByteArrayInputStream in = new ByteArrayInputStream(_in);
			Source source = new StreamSource(in);
			try
			{
				transformer.transform(source, result);
				out.flush();
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
	}
}
