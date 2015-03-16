package org.dykman.gossamer.hibernate;

import java.util.Map;

import org.dykman.gossamer.log.LogManager;
import org.hibernate.SessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SessionFactoryManagerFactory implements ApplicationContextAware {

	ApplicationContext applicationContext;
	Map<String, org.hibernate.SessionFactory> factories = null;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;	
	}
	
	public SessionFactoryManager createSessionFactoryManager() {
		if(factories == null) {
			synchronized(this) {
				if(factories == null) {
					factories = new java.util.concurrent.ConcurrentHashMap<String, SessionFactory>();
					String [] ss = applicationContext.getBeanNamesForType(org.hibernate.SessionFactory.class);
					LogManager logManager = (LogManager) applicationContext.getBean("logManager");
					for(String s : ss) {
						String id = s;
						if(s.endsWith("SessionFactory")) {
							id = s.substring(0,s.lastIndexOf('S'));
						}
						SessionFactory sf = (SessionFactory) applicationContext.getBean(s);
						logManager.writeLog("hibernate.log", "adding Hibernate SessionFactoryManager `" + id + "'");
						factories.put(id, sf);
					}
				}
			}
		}
		SessionFactoryManager sfm = new SessionFactoryManager();
		sfm.setFactories(factories);
		return sfm;
	}

}
