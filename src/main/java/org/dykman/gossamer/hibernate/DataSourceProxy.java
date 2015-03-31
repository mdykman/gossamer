package org.dykman.gossamer.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
public class DataSourceProxy
{
	private SessionFactory sessionFactory = null;
	private Session session = null;
	
	public void setSessionFactory(SessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
	}

	public synchronized Session getSession()
	{
		if(session == null)
		{
			session = this.sessionFactory.openSession();
		}
		return session;
	}

	public void close()
	{
		if(session != null)
		{
			session.close();
		}
	}

}
