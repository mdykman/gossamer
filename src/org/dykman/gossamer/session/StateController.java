package org.dykman.gossamer.session;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.Transaction;
import org.springframework.context.ApplicationContext;

public class StateController implements UserState
{
	Set<String> keys = new HashSet<String>();
	Map<String,SessionBean> data = new HashMap<String, SessionBean>();
	ApplicationContext context;
	Transaction transaction;
	String sessionId;
	
	public void setData(Map<String,SessionBean> data)
	{
		this.data = data;
	}
	
	public void set(String s, Object o)
	{
		keys.add(s);
		SessionBean b = data.get(s);
		if(b!=null) {
			b.setValue(b);
		}
		else {
			b = new SessionBean();
			b.setName(s);
			b.setValue(b);
			data.put(s,b);
		}
	}
	
	public void remove(String string )
	{
		keys.add(string);
		data.remove(string);
	}
	
	public Object get(String string )
	{
		SessionBean b = data.get(string);
		if(b == null) {
			return null;
		}
		else {
			return b.getValue();
		}
	}
	
	public Set<String> dirty()
	{
		return keys;
	}

}
