package org.dykman.gossamer.session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.dykman.gossamer.core.MyThread;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class DistributedSessionFilter implements Filter
{
	String sector = null;
	boolean log = false;
	ApplicationContext applicationContext;
	String sessionLabel = "GOSSAMER-SESSION";
	int MAX_MERGE_RETRY = 3;
	ServletContext context;
	
	static final Object WRITE_LOCK = new Object();
	public void init(FilterConfig config) throws ServletException
	{
		Thread thread = new MyThread("foo");
		thread.start();
		synchronized (WRITE_LOCK)
        {
	        
        }

		
		
		context = config.getServletContext();
//		sector = config.getInitParameter("sector");
		String v = config.getInitParameter("merge-retry");
		if(v != null) {
			MAX_MERGE_RETRY = Integer.parseInt(v);
		}
		v = config.getInitParameter("session-label");
		if(v != null) {
			sessionLabel = v;
		}
		applicationContext = WebApplicationContextUtils
			.getWebApplicationContext(context);
	}

	@SuppressWarnings("unchecked")
	public void doFilter(ServletRequest req, ServletResponse res,
	        FilterChain chain) 
		throws IOException, ServletException
	{
		SessionFactory factory = (SessionFactory) 
			applicationContext.getBean("gsessionSessionFactory");
		
		Session session = null;
		Transaction transaction = null;
		
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)res;
		
		try {
			session = factory.openSession();
			transaction = session.beginTransaction();
			String sessionId;
			Map<String,SessionBean> data = 
				new HashMap<String, SessionBean>();
			List<SessionBean> l;

			Cookie cookie = getCookie(request);
			if(cookie == null) {
				sessionId = (String)session
					.createSQLQuery("SELECT UUID()").uniqueResult();
				initData(data,sessionId);
				l = new ArrayList<SessionBean>(0);
			}
			else {
				sessionId = cookie.getValue();
				Query query = session.createQuery(
					"FROM SessionBean WHERE gsid = :gsid");
				query.setString("gsid", sessionId);
				l = query.list();
				for(SessionBean b : l) {
					data.put(b.getName(), b);
				}
			}
			GossamerSession gs= (GossamerSession)applicationContext.getBean("gsession");
			gs.setId(sessionId);
			gs.setData(data);
			gs.setServletContext(context);
			gs.setHttpSession(request.getSession());
			Req rq = new Req(request,gs);
			
			chain.doFilter(rq, response);

			for(SessionBean b : l)
			{
				String name = b.getName();
				if(!data.containsKey(name)) {
					session.delete(b);
				}
				else {
					data.remove(name);
					try
					{
						session.merge(b);
					}
					catch(org.hibernate.StaleObjectStateException e)
					{
// read , replace, retry						
						SessionBean a = (SessionBean)
							session.get(b.getClass(), b);
						a.setValue(b.getValue());
						merge(session,b);
					}
				}
			}
			
			for(SessionBean newb : data.values()) {
				newb.setGsid(sessionId);
				try {
					session.save(newb);
				}
				catch(org.hibernate.NonUniqueObjectException e)
				{
					merge(session,newb);
				}
			}
			transaction.commit();
			Cookie cc = new Cookie(sessionLabel,sessionId);
			response.addCookie(cc);
		}
		catch(Exception e)
		{
			if(transaction != null){
				transaction.rollback();
			}
		}
		finally
		{
			if(session != null)
			{
				session.close();
			}
		}
	}


	public void destroy()
	{
	}
	
	protected void initData(Map<String,SessionBean> data,String sessionId)
	{
		long now = System.currentTimeMillis();
		SessionBean sb = new SessionBean();
		sb.setGsid(sessionId);
		sb.setName(GossamerSession.created);
		sb.setValue(now);
		data.put(GossamerSession.created, sb);

		sb = new SessionBean();
		sb.setGsid(sessionId);
		sb.setName(GossamerSession.maxActiveL);
		sb.setValue(1800);
		data.put(GossamerSession.maxActiveL, sb);

		sb = new SessionBean();
		sb.setGsid(sessionId);
		sb.setName(GossamerSession.lastActiveL);
		sb.setValue(now);
		data.put(GossamerSession.lastActiveL, sb);
		
	}
	
	protected Cookie getCookie(HttpServletRequest req)	{
		Cookie[] cc = req.getCookies();
		for(Cookie c : cc) {
			if(sessionLabel.equals(c.getName())) {
				return c;
			}
		}
		return null;
	}

	protected void merge(Session session, SessionBean b)
		throws Exception
	{
		merge(session, b,0);
	}
	protected void merge(Session session, SessionBean b, int n)
		throws Exception
	{
		try {
			session.merge(b);
		}
		catch(org.hibernate.StaleObjectStateException e)
		{
			if(++n > MAX_MERGE_RETRY) {
System.out.println("WARNING: failed to update session " 
	+ b.getGsid() + " '" + b.getName() + "'");
				return;
			}
			SessionBean a = (SessionBean)session.get(b.getClass(), b);
			a.setValue(b.getValue());
			merge(session,a,n);
		}
	}
	
	
	static class Req extends javax.servlet.http.HttpServletRequestWrapper
	{

		GossamerSession gos;
		public Req(HttpServletRequest request,GossamerSession gos)
        {
	        super(request);
	        this.gos = gos;
        }
		@Override
		public HttpSession getSession()
		{
			return gos;
		}
	
	}
}
