package org.dykman.gossamer.webapp;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.dykman.gossamer.channel.ChannelManager;
import org.dykman.gossamer.core.Controller;
import org.dykman.gossamer.core.PageController;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class SessionListener implements HttpSessionListener
{

	public void sessionCreated(HttpSessionEvent arg0)
	{
	}

	public void sessionDestroyed(HttpSessionEvent arg0)
	{
		HttpSession session = arg0.getSession();
		Enumeration<?> en = session.getAttributeNames();
		while(en.hasMoreElements())
		{
//			Object o = en.nextElement();
//			System.out.println("   on destroy: key = " + o.toString());
		}
		ServletContext context = arg0.getSession().getServletContext();
		Controller controller = (Controller) session.getAttribute("clientController");

		if(controller != null)
		{
			ChannelManager supervisor = (ChannelManager) WebApplicationContextUtils
				.getWebApplicationContext(context).getBean("channelManager");
	
			for(PageController pg : controller.getPageControllers())
			{
				supervisor.removeFromAllChannels(pg);
			}
		}
	}
}
