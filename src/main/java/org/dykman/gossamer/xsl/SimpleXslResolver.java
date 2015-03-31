package org.dykman.gossamer.xsl;

import org.dykman.gossamer.core.StringContainer;
import org.dykman.gossamer.device.ClientDeviceProfile;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SimpleXslResolver implements XslResolver, ApplicationContextAware
{
	ApplicationContext applicationContext;
	
	protected String basePath;
	private String formatAttribute;
	private String format = null;
	private StyleResolver styleResolver;
	
	public void setFormatAttribute(String formatAttribute) {
    	this.formatAttribute = formatAttribute;
    }
	
	public void setFormat(String s) {
		format = s;
	}
	
	public String getFormat() {
		return format;
	}

	public void setStyleResolver(StyleResolver styleResolver) {
    	this.styleResolver = styleResolver;
    }

	public String getBasePath() {
    	return basePath;
    }

	public void setBasePath(String basePath) {
		// TODO parse basePath, allowing variables for host and/or context
    	this.basePath = basePath;
    }

	public String resolveXslUrl(ClientDeviceProfile deviceProfle, String view)
	{
		StringContainer baseUrl = (StringContainer)
			applicationContext.getBean("baseUrl");
	
		//System.out.println("BASE URL: " + baseUrl.getValue());
		
		StringBuilder buffer = new StringBuilder(baseUrl.getValue());
		
		buffer.append(basePath);
		buffer.append(resolveXslPath(deviceProfle, view));

//		buffer.append(view);
		return buffer.toString();
	}

	public String resolveXslPath(ClientDeviceProfile deviceProfile, String view)
	{
		StringBuilder buffer = new StringBuilder();

		if(format == null)
			format = deviceProfile.getAttribute(formatAttribute);

		buffer.append('/').append(styleResolver.getStyle()).append('/').append(format)
			.append('/').append(view);
		
		if(!buffer.toString().endsWith(".xsl")) buffer.append(".xsl");
		return buffer.toString();
	}

	public void setApplicationContext(ApplicationContext applicationContext)
    {
    	this.applicationContext = applicationContext;
    }

}
