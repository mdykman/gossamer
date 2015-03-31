package org.dykman.gossamer.handler;

import java.util.ArrayList;
import java.util.List;

import org.dykman.gossamer.core.RequestProcessor;
import org.dykman.gossamer.core.RequestProcessorAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SimpleHandlerFactory implements ApplicationContextAware,
        HandlerFactory
{
	ApplicationContext	applicationContext;
	List<Decorator>	   decorators	= new ArrayList<Decorator>();
	RequestProcessor	requestProcessor;

	public void setRequestProcessor(RequestProcessor requestProcessor)
	{
		this.requestProcessor = requestProcessor;
	}

	public void setDecorators(List<?> list)
	{
		for (Object oo : list)
		{
			decorators.add((Decorator) oo);
		}
	}

	public void setApplicationContext(ApplicationContext applicationContext)
	{
		this.applicationContext = applicationContext;
	}

	public Object createHandler(String name)
	{
//System.out.println("in SimpleHandlerFactory");
		String beanId = name + "Handler";
		Object handler = null;

		if (applicationContext.containsBean(beanId))
		{
			handler = applicationContext.getBean(beanId);
			if (handler instanceof HandlerFactoryAware) {
				((HandlerFactoryAware) handler).setHandlerFactory(this);
			} 
			if (handler instanceof RequestProcessorAware) {
				((RequestProcessorAware) handler)
				        .setRequestProcessor(requestProcessor);
			}

			for (Decorator en : decorators) {
				en.decorate(handler);
			}
		}
		return handler;
	}

}
