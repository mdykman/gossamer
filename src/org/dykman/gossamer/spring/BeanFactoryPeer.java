package org.dykman.gossamer.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class BeanFactoryPeer implements ApplicationContextAware
{

	BeanFactory beanFactory;
	public void setApplicationContext(ApplicationContext applicationContext)
	        throws BeansException {
		beanFactory = new GossamerBeanFactory(applicationContext);	
	}
	
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

}
