package org.dykman.gossamer.hibernate;

import org.hibernate.Session;

public interface HibernateAware
{
	public void setHibernateSession(Session session);
}
