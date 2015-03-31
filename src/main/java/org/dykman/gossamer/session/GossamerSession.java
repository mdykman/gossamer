package org.dykman.gossamer.session;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.dykman.gossamer.core.GossamerException;

@SuppressWarnings("deprecation")
public class GossamerSession implements HttpSession
{
	private int maxInactive = 1800;
	private long lastActive;
	private long creationTime;
	private Map<String,SessionBean> data;
	private String id;
	private ServletContext context;
		
	private HttpSession httpSession;
	
	public static final String created = "__gossamer_session_created";
	public static final String lastActiveL = "__gossamer_session_last_active";
	public static final String maxActiveL = "__gossamer_session_max_active";
	
	public GossamerSession()
	{
	}
	public void setId(String id)
	{
		this.id = id;
	}
	public void setData(Map<String,SessionBean> data)
	{
		this.data = data;
		setLastActive(System.currentTimeMillis());
	}
	public Object getAttribute(String arg0)
	{
		return data.get(arg0).getValue();
	}


    @SuppressWarnings("rawtypes")
	public Enumeration getAttributeNames() {
		return new Enum(data.keySet()); 
	}

	public long getCreationTime() {
		return creationTime;
	}

	public String getId() {
		return id;
	}

	public long getLastAccessedTime() {
		return lastActive;
	}

	public int getMaxInactiveInterval() {
		return maxInactive;
	}

	public void setServletContext(ServletContext context) {
		this.context = context;
	}
	public ServletContext getServletContext() {
		return context;
	}

	public HttpSessionContext getSessionContext() {
		throw new GossamerException("not implemented"); 
	}

	public Object getValue(String arg0) {
		return getAttribute(arg0);
	}

	public String[] getValueNames() {
		return data.keySet().toArray(
			new String[data.keySet().size()]);
	}

	public void invalidate() {
	}

	public boolean isNew() {
		return false;
	}

	public void putValue(String arg0, Object arg1) {
		this.setAttribute(arg0, arg1);
	}

	public void removeAttribute(String arg0) {
		data.remove(arg0);
	}

	public void removeValue(String arg0) {
		removeAttribute(arg0);
	}

	public void setAttribute(String arg0, Object arg1) {
		SessionBean b = data.get(arg0);
		if(b == null) {
			b = new SessionBean();
			b.setGsid(id);
			b.setName(arg0);
			data.put(arg0, b);
		}
		b.setValue(arg1);
	}

	public void setMaxInactiveInterval(int arg0) {
		data.get(maxActiveL).setValue(arg0);
		maxInactive = arg0;
	}
	
	protected void setLastActive(long l)
	{
		data.get(lastActiveL).setValue(l);
		lastActive = l;
	}
	
	protected void setCreatedActive(long l)
	{
		data.get(created).setValue(l);
		creationTime = l;
	}
	
	static class Enum implements Enumeration<String>
	{
		Iterator<String> it;
		public Enum(Set<String> k) {
			it = k.iterator();
		}

		public boolean hasMoreElements() {
			return it.hasNext();
        }

		public String nextElement() {
			return it.next();
        }
		
	}

	public HttpSession getHttpSession()
    {
    	return httpSession;
    }
	public void setHttpSession(HttpSession httpSession)
    {
    	this.httpSession = httpSession;
    }
}
