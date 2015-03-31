package org.dykman.gossamer.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.dykman.gossamer.handler.HandlerFactory;
import org.dykman.gossamer.webapp.NotFoundException;
import org.dykman.gossamer.webapp.PathInfo;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SimpleRequestProcessor implements RequestProcessor, ApplicationContextAware
{
	protected ApplicationContext applicationContext;
	protected Controller controller;
	protected HandlerFactory handlerFactory;
	protected PathInfo pathInfo;
	
	public void setPathInfo(PathInfo pi) {
		this.pathInfo = pi;
	}
	public void setHandlerFactory(HandlerFactory handlerFactory)
	{
		this.handlerFactory = handlerFactory;
	}
	public void setController(Controller controller)
	{
		this.controller = controller;
	}
	
	public void setApplicationContext(ApplicationContext ac)
	{
		applicationContext = ac;
	}

	// the /rq interface
	public Object processRequest(String requestPath, Map<String,String> params)
	{
		PageController controller = new PageController();
		processRequest(controller, requestPath, null, params);
		return controller.dequeue();
	}

	// the DWR access point
	public void processRequest(PageController pg, String requestPath,String anon, Map<String,String> params)
	{
		ResponseBean[] rr = invokeHandler(pg,requestPath, anon,params);
		if(rr != null)
		{
			for(ResponseBean rb : rr)
			{
				pg.enqueue(rb);
			}
		}
	}
	
	protected ResponseBean[] invokeHandler(PageController pg,String requestPath, String anon, Map<String,String> params)
	{
		ResponseContainer responses = new ResponseContainerImpl();
		Object result = null;

//		int n = requestPath.indexOf('/',1);
		String [] pp = requestPath.split("[/]", 4);
		PathInfo pi = (PathInfo) applicationContext.getBean("handlerPathInfo");
		pi.setHandlerPathInfo( pp.length > 3 ? "/" + pp[3] : "");
		
		Object handler = handlerFactory.createHandler(pp[1]);

		if(handler == null) {
			throw new NotFoundException("no handler found for: " + requestPath);
		}

		// TODO:: - this block is obsolete, I'm pretty sure
		if(handler instanceof PageControllerAware) {
			PageControllerAware pia = (PageControllerAware) handler;
			pia.setPageController(pg);
		}
		
		try {
			Method method = handler.getClass().getMethod(pp[2], Map.class);
			if(method == null) {
				throw new NotFoundException("no method found : " + requestPath + "." + pp[2]);
			}
			result = method.invoke(handler, new Object[] {params});
			result = result == null ? "" : result;
			// TODO:: - this anon noise is also obsolete
			if(result != null) {
				if(anon != null) {
					responses.enqueue(anon, null, result);
				}
				else {
					responses.enqueue(pp[1], pp[2], result);
				}
			}
		}
		catch(NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		catch(IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		catch(InvocationTargetException e) {
			Throwable cause = e.getCause();
			if(cause instanceof GossamerException) {
				throw (GossamerException)cause;
			}
			else {
				throw new GossamerException(cause);
			}
		}
		return responses.getResponse();
	}
}
