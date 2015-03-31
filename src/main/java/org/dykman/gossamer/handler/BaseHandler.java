package org.dykman.gossamer.handler;

import org.dykman.gossamer.channel.ChannelManager;
import org.dykman.gossamer.core.Controller;
import org.dykman.gossamer.core.PageController;
import org.dykman.gossamer.core.PageControllerAware;
import org.hibernate.Session;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class BaseHandler implements ApplicationContextAware, PageControllerAware
{
	protected ApplicationContext applicationContext;
	protected Session hibernateSession;
//	private static SessionFactory sessionFactory;
	
	protected PageController pageController;
	
	
	static {
//		sessionFactory = new Configuration().configure().buildSessionFactory();
	}
	
	public void setPageController(PageController pi)
	{
		pageController = pi;
	}
	public void setApplicationContext(ApplicationContext applicationContext)
	{
		this.applicationContext  = applicationContext;
	}
	
	protected ChannelManager getChannelManager()
	{
		return  (ChannelManager) applicationContext.getBean("channelManager");
	}

	protected Controller getController()
	{
		return  (Controller) applicationContext.getBean("clientController");
	}
}
