package org.dykman.gossamer.webapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Properties;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dykman.gossamer.Constants;
import org.dykman.gossamer.core.GossamerException;
import org.dykman.gossamer.device.ClientDeviceProfile;
import org.dykman.gossamer.device.DeviceProfileFactory;
import org.dykman.gossamer.log.LogManager;
import org.dykman.gossamer.util.Container;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public abstract class GossamerServlet extends HttpServlet
{
	private static final long serialVersionUID = 13434534673454L;

	protected ApplicationContext applicationContext;
	protected String poweredBy = Constants.GOSSAMER_VERSION_STRING;
	protected DeviceProfileFactory deviceProfileFactory; 
//	protected SiteProperties	siteProperties;
	protected Properties gossamerProperties = new Properties();
	protected LogManager logManager;
	
	protected File GossamerInstallationBase;
	
	protected File logBase;
	protected int outputBufferSize = 4096;

	protected void addCredit(String credit) {
		poweredBy = new StringBuilder(poweredBy).append(", ").append(credit).toString();
	}
	@Override
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
		ServletContext context = config.getServletContext();
		applicationContext = WebApplicationContextUtils
			.getWebApplicationContext(context);
		
		GossamerInstallationBase = new File((String) applicationContext.getBean("installBase"));
		File gpf = new File(GossamerInstallationBase ,"webapp/WEB-INF/gossamer.properties");
		try {
			InputStream inputStream = new FileInputStream(gpf);
			gossamerProperties.load(inputStream);
		} catch(Exception e) {
			throw new GossamerException(e);
		}
//		siteProperties = (SiteProperties) applicationContext
//			.getBean("siteProperties");
		

		logManager = (LogManager) applicationContext.getBean("logManager");
		
		deviceProfileFactory = (DeviceProfileFactory) applicationContext.getBean("deviceProfileFactory");
	}
	
	protected void writeLog(String log, String message) {
		logManager.writeLog(log, message);
	}
	
	protected void setCacheHeaders(HttpServletRequest request,HttpServletResponse response) {
		
	}

	public boolean serve(
		HttpServletRequest request, 
		HttpServletResponse response,
		ClientDeviceProfile device)
		throws IOException, ServletException {
		return serve(request,response);
	}
	public boolean serve(HttpServletRequest request, HttpServletResponse response) 
		throws IOException, ServletException {
			throw new GossamerException("not implemented");
	}
	
	@Override
	public final void service(HttpServletRequest request, HttpServletResponse response) 
		throws IOException, ServletException
	{
		response.setHeader("X-Powered-By",poweredBy + " " + Constants.GOSSAMER_COPYRIGHT_STRING); 
		ServletRequestHolder rd = (ServletRequestHolder) applicationContext
			.getBean("requestHolder");
		rd.setServletRequest(request);
			
		ServletResponseHolder rs = (ServletResponseHolder) applicationContext
			.getBean("responseHolder");
		rs.setServletResponse(response);

		String userAgent = request.getHeader("User-Agent");

		if (userAgent != null)
		{
			ClientDeviceProfile device = deviceProfileFactory.getDeviceProfile(userAgent);

			if(device == null) {
//				logManager.writeLog("device.log", "failed to get a device from the factory");
			} else {
				Container dc = (Container) applicationContext
					.getBean("deviceContainer");
				dc.setObject(device);
			}
//System.out.println("calling SERVE");		

			serve(request,response,device);
		}
		else
		{
			serve(request,response);
		}
	}
	protected boolean serve(File file, HttpServletRequest request, HttpServletResponse response)
    	throws IOException, ServletException {
		return this.serve(file, request,response,null);
   }

	public boolean serve(File file, HttpServletRequest request, HttpServletResponse response,String contentType)
	        throws IOException, ServletException
	{
//		getServletContext().getRequestDispatcher("default").forward(request, response);
//		return true;
		
		
		if(!file.exists()) {
			response.setStatus(404);
			return false;
		}
		response.setStatus(200);
		
		if(contentType == null) {
			FileTypeMap ftm = MimetypesFileTypeMap.getDefaultFileTypeMap();
			response.setContentType(ftm.getContentType(file));
		} else {
			response.setContentType(contentType);
		}

		RandomAccessFile ra = new RandomAccessFile(file.getAbsoluteFile(), "r");
		response.setContentLength((int) ra.length());

		setCacheHeaders(request,response);

		byte[] bb = new byte[outputBufferSize];
		ra.seek(0);
		OutputStream out = response.getOutputStream();
		int n;
		while ((n = ra.read(bb)) != -1)
		{
			out.write(bb, 0, n);
		}
		ra.close();
		out.flush();
		return true ;
		
	}
}
