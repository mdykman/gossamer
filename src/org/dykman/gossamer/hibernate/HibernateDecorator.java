package org.dykman.gossamer.hibernate;

import org.dykman.gossamer.handler.Decorator;

public class HibernateDecorator implements Decorator
{
	DataSourceProxy  proxy;
	
	public void setDataProxy(DataSourceProxy ds)
	{
		proxy = ds;
	}
	
	public void decorate(Object o)
	{
		if(o instanceof HibernateAware)
		{
			HibernateAware ha = (HibernateAware) o;
			ha.setHibernateSession(proxy.getSession());
		}
	}

}
