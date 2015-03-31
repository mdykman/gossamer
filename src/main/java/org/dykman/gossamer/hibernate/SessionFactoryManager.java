package org.dykman.gossamer.hibernate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

public class SessionFactoryManager
{
	Map<String, org.hibernate.SessionFactory> factories 
		= new HashMap<String,org.hibernate.SessionFactory>();
	Map<String,ThreadLocal<Session>> sessions = 
		new HashMap<String, ThreadLocal<Session>>();
	
	List<Session> csessions = new ArrayList<Session>();
	
	public SessionFactoryManager() {
		
	}
	public Session get(String name) {
		return getSession(name);
	}
	
	public Session getSession(String name)
	{
		Session session = null;
		ThreadLocal<Session> s = sessions.get(name);
		if(s!= null) {
			session = s.get();
		} else {
			synchronized(this) {
				if(s == null) {
					s = new ThreadLocal<Session>();
					sessions.put(name, s);
				}
			}
		}
		
		if(session == null) {
			org.hibernate.SessionFactory f = factories.get(name);
			if(f != null) synchronized(s) {
				// one last check to see if the previous lock-holder did the job
				if((session = s.get()) == null) {
					session = f.openSession();
					s.set(session);
					csessions.add(session);
				} 
			}
		}
		return session;
	}
	
	public void closeAll() {
		for(Session s : csessions) {
			try {
				s.close();
			} catch(Exception e) {
System.err.println("failed to close a connection");
//				e.printStackTrace();
			}
		}
	}

	public void setFactories(Map<String, org.hibernate.SessionFactory> factories) {
    	this.factories = factories;
    }
}
