package org.dykman.gossamer.xsl;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dykman.gossamer.core.OutputStreamAware;
import org.dykman.gossamer.core.ViewManager;
import org.dykman.gossamer.device.OutputDevice;
import org.dykman.gossamer.webapp.Transformer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class XslDecoratorDevice implements OutputDevice,
        ApplicationContextAware, OutputStreamAware
{
	TransformerFactory	transformerFactory;
	ApplicationContext	applicationContext;
	OutputStream	   outputStream;

	public void setOutputStream(OutputStream outputStream)
	{
		this.outputStream = outputStream;
	}

	public void setApplicationContext(ApplicationContext applicationContext)
	{
		this.applicationContext = applicationContext;
	}

	public void setTransformerFactory(TransformerFactory transformerFactory)
	{
		this.transformerFactory = transformerFactory;
	}

	public void execute(HttpServletRequest request,
	        HttpServletResponse response, byte[] content)
	{
		ViewManager viewManager = (ViewManager) applicationContext
		        .getBean("viewManager");
		String view = viewManager.getView();

		if(content == null || content.length == 0)
		{
			// prefectly legal: ie status 304 resopnses
			// just skip all the noise
		} else if (isXMLType(response.getContentType()) && view != null) {
			HttpServletRequest req = (HttpServletRequest) request;
			String userAgent = req.getHeader("User-Agent");
			Transformer transformer = transformerFactory.createTransformer(
			        userAgent, response.getContentType(), view);
			response.setContentType(transformer.getContentType());
			if (outputStream != null)
			{
				try
				{
					transformer.transform(content, outputStream);
				} 
				catch (Exception e)
				{
					System.out.println("WARNING: exception while transforming to output stream: " + e.getMessage());
					e.printStackTrace();
				}

			} 
			else
			{
				System.out
				        .println("WARN: outputstream closed while writing transform");
			}
		} 
		else
		{
			if (outputStream != null)
			{
				try
				{
					outputStream.write(content);
				} 
				catch (IOException e)
				{
					System.out.println("WARNING: IOException writing to output stream: " + e.getMessage() );
					e.printStackTrace();
				}
			}
		}
	}

	public boolean isXMLType(String type)
	{
		if (type == null)
			return false;

		return type.startsWith("text/xml")
		        || type.startsWith("application/xml");
	}
}
