package org.dykman.gossamer.session;

import java.io.Serializable;

public class SessionBeanKey  implements Serializable
{
    transient private static final long serialVersionUID = 3358863832509265545L;

	public String gsid;
    public String name;
	
    public SessionBeanKey() {
    }
    
    public SessionBeanKey(String gsid,String name) {
    	this.gsid = gsid;
    	this.name = name;
    }
    public String getName() {
    	return name;
    }
	
	public void setName(String name) {
    	this.name = name;
    }

	public void setGsid(String gsid) {
		this.gsid = gsid;
	}
	
	public String getGsid() {
    	return gsid;
    }

	@Override
	public int hashCode()
	{
		return gsid.hashCode() + name.hashCode();
	}
	@Override
	public boolean equals(Object o)
	{
		boolean result = false;
		if(o instanceof SessionBeanKey)
		{
			SessionBeanKey lhs = (SessionBeanKey)o;
			result = gsid.equals(lhs.gsid) && name.equals(lhs.name);
		}
		return result;
	}
}
