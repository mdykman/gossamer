package org.dykman.gossamer.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

public class GossamerBeanFactory implements BeanFactory
{
//	private ApplicationContext context;
	private ApplicationContext context;
	public GossamerBeanFactory(ApplicationContext context) {
		this.context = context;
	}

	public boolean containsBean(String arg0)
	{
		return context.containsBean(arg0);
	}

	public String[] getAliases(String arg0)
	{
		return context.getAliases(arg0);
	}

	public Object get(String arg0) throws BeansException
	{
		return this.getBean(arg0);
	}
   
	public Object getBean(String arg0) throws BeansException
	{
		return context.getBean(arg0);
	}


	public Object getBean(String arg0, Object... arg1) throws BeansException
	{
		return context.getBean(arg0,arg1);
	}

	public Class<?> getType(String arg0) throws NoSuchBeanDefinitionException
	{
		return context.getType(arg0);
	}

	public boolean isPrototype(String arg0)
	        throws NoSuchBeanDefinitionException
	{
		return context.isPrototype(arg0);
	}

	public boolean isSingleton(String arg0)
	        throws NoSuchBeanDefinitionException
	{
		return context.isSingleton(arg0);
	}

	
	@SuppressWarnings("rawtypes")
	public boolean isTypeMatch(String arg0, Class arg1)
	        throws NoSuchBeanDefinitionException
	{
		return context.isTypeMatch(arg0, arg1);
	}

	@Override
	public <T> T getBean(Class<T> arg0) throws BeansException {
		// TODO Auto-generated method stub
		return context.getBean(arg0);
	}

	@Override
	public <T> T getBean(String arg0, Class<T> arg1) throws BeansException {
		// TODO Auto-generated method stub
		return context.getBean(arg0,arg1);
	}

}
