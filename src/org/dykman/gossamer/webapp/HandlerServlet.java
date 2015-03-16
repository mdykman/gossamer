package org.dykman.gossamer.webapp;

import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.dykman.gossamer.core.GossamerException;
import org.dykman.gossamer.core.Idler;
import org.dykman.gossamer.core.PageController;
import org.dykman.gossamer.core.Renderer;
import org.dykman.gossamer.core.RequestProcessor;
import org.dykman.gossamer.core.ResponseException;
import org.dykman.gossamer.core.ViewManager;
import org.dykman.gossamer.device.ClientDeviceProfile;
import org.dykman.gossamer.xml.DocumentRenderer;


public class HandlerServlet extends GossamerServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4489247386356950752L;
	private  String xmlName = "xml";
	private  String renderName = "render";
	private  String jsonName = "json";
	private  String cometName = "comet";

	@Override
	public void init(ServletConfig config)
		throws ServletException {
		super.init(config);
		String s = config.getInitParameter("jsonPath");
		jsonName = s != null ? s : jsonName;
		s = config.getInitParameter("xmlPath");
		xmlName = s != null ? s : xmlName;

		s = config.getInitParameter("renderPath");
		renderName = s != null ? s : renderName;

		s = config.getInitParameter("cometPath");
		cometName = s != null ? s : cometName;

	}

	protected PageController getPageController(HttpServletRequest request) {		
		String pageId = request.getParameter("gcpid");
		if(pageId == null) return new PageController();
		
		PageController pg;
		HttpSession session = request.getSession();
		@SuppressWarnings("unchecked")
		Map<String, PageController> m = (Map<String, PageController>)
			session.getAttribute("gossamer-page-controllers");
		if(m == null) {
			m = new WeakHashMap<String, PageController>();
			session.setAttribute("gossamer-page-controllers", m);
		}
		pg = m.get(pageId);
		if(pg == null) {
			pg = new PageController();
			m.put(pageId, pg);
		}
		return pg;
	}
	
	public void cometService(HttpServletRequest request, HttpServletResponse response, ClientDeviceProfile device) 
		throws Exception {
		Idler idler = (Idler) applicationContext.getBean("cometIdler"); 
		idler.setRequest(request);
		PageController pg = getPageController(request);
		Renderer renderer = (Renderer) applicationContext.getBean("jsonRenderer");
		response.setContentType(renderer.getContentType());
		Writer writer = response.getWriter();
		synchronized(pg) {
			Object o = pg.dequeue();
			if(o == null) {
				if(idler.isSuspended()) {
					renderer.render("nothing", writer);
				} else {
					idler.waitFor();
				}
			} else {
				writer.write(o.toString());
			}
		}
	}
	
	@Override
	public boolean serve(HttpServletRequest request, HttpServletResponse response, ClientDeviceProfile device)
		throws IOException, ServletException {

		String ruri = request.getRequestURI();
		int t = ruri.indexOf('/', 1);
		String selector = ruri.substring(1, t);
		String suri = ruri.substring(t+1);
		Object o = 	null;
		ViewManager viewManager = (ViewManager) applicationContext.getBean("viewManager");
		StringBuilder sbTrace = new StringBuilder();
//
//		if (ruri == null)
//			sbTrace.append("<null uri>");
//		else
			sbTrace.append(ruri);
		
		try {
			try {
				if(cometName.equals(selector)) {
					cometService(request,response,device);
				}
				
				if(suri.length() > 0) {
					PageController pg = new PageController();
					RequestProcessor processor = 
						(RequestProcessor) applicationContext.getBean("requestProcessor");
					processor.processRequest(pg,suri,null,prepareParameters(request));
					o = pg.dequeue();
				}
			} catch(Exception e) {
				ResponseException re = findResponseException(e);
				if(re != null) throw re; 
				throw new GossamerException(e);
			} 
		}
		catch(RedirectException e) {
			viewManager.redirect(getRedirectURL(request, e.getUrl()));
			viewManager.setStatus(e.getStatus());
			viewManager.redirect(e.getUrl());
//			response.setHeader("Location", getRedirectURL(request, e.getUrl()));
		}
		catch(GossamerException e) {
			Map<String, String> nf = new HashMap<String, String>();
			nf.put("message",e.getMessage());
			o = nf;
			System.err.println(sbTrace.toString() + ": gossamer exception: " + e.getMessage());
			e.printStackTrace();
			viewManager.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			viewManager.setView("error.html");
		}
		catch(Exception e) {
			viewManager.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			viewManager.setView("error.html");
			System.err.println(sbTrace.toString() + ": exception: " + e.getMessage());
			e.printStackTrace();
		}
		
		
		try {
			int status = viewManager.getStatus();
			if(status != 0) response.setStatus(status);
			if(status == 302 || status == 302 || status == 307) {
				response.setHeader("Location", getRedirectURL(request, viewManager.getRedirectUrl()));
			}
			
			if(o != null && o != RequestProcessor.RESPONSE_COMPLETE) {

				String view = viewManager.getView();

				if (view == null)
					sbTrace.append(" (<null view>)");
				else
					sbTrace.append(" (" + view + ")");

				Renderer renderer;
				if("xml".equalsIgnoreCase(view)) {
					selector = xmlName;
				} else if("json".equalsIgnoreCase(view)) {
					selector = jsonName;
				}
				
				if(selector.equals(jsonName)) {
					sbTrace.append(" [json]");
					renderer = (Renderer) applicationContext.getBean("jsonRenderer");
				} else {
					DocumentRenderer documentRenderer = 
						(DocumentRenderer) applicationContext.getBean("documentRenderer");
					
					// no view, render XML
					if(selector.equals(xmlName))
					{
						sbTrace.append(" [xml<null view>]");
						view = null;
					}
					else {
						//set the view
						documentRenderer.setView(view);

						// force serverside render
						if(selector.equals(renderName))
						{
							sbTrace.append(" [render]");
							documentRenderer.setForce(true);
						}
					}

					renderer = documentRenderer; 

					sbTrace.append(": " + renderer.getClass().getName());
				}

				// interesting bit of hackery
//				if (selector.equals(renderName) && view != null && view.endsWith(".html"))
//					response.setContentType("text/html");
//				else
				response.setContentType(renderer.getContentType());

				Writer out = response.getWriter();
				renderer.render(o, out,
						"true".equals(gossamerProperties.get("gossamer.xml.indent")) 
						|| request.getParameter("indent") != null ? true : false);
				out.flush();
			}

			// System.err.println("OK: " + sbTrace.toString());
		}
		catch(Exception e) {
			System.err.println("caught exception in HandlerServlet / " + sbTrace.toString());

			/*
			if(o != null) {
				// this always comes through as java.util.LinkedHashMap rather than expected class..
				System.err.println("while procesing " + o.getClass().getName());
			}
			*/

			System.err.println("WARNING: " + e.getClass().getName() 
				+ " while writing response: "
				+ e.getMessage());

			e.printStackTrace();
		}
		return true;
	}
	
	
	protected ResponseException findResponseException(Exception e) {
		Throwable th = e;
		ResponseException re = null;
		while(th != null && re == null) {
			if(th instanceof ResponseException) {
				re = (ResponseException) th;
			}
			th = th.getCause();
		}
		return re;
	}


	protected String getRedirectURL(HttpServletRequest request, String url)
		throws IOException {
		String r;
		if(url.startsWith("/"))
		{
			URL u = new URL(request.getRequestURL().toString());
			String ct = request.getContextPath();
			if(ct != null && ct.length() > 1) {
				u = new URL(u,ct + url);
			}
			else {
				u = new URL(u,url);
			}
			
			r = u.toExternalForm();
		}
		else
		{
			r = url;
		}
		return r;
	}
	
	protected Map<String,String> prepareParameters(ServletRequest req) {
		Map<String,String> result = new HashMap<String, String>();
		Enumeration<?> en = req.getParameterNames();
		while(en.hasMoreElements())
		{
			String key = (String)en.nextElement();
			result.put(key, req.getParameter(key));
		}
		return result;
	}
	
	public static class NotFound {
		private String message;

		public String getMessage()
        {
        	return message;
        }

		public void setMessage(String message)
        {
        	this.message = message;
        }
	}
}
