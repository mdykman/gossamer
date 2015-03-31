package org.dykman.gossamer.session;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SessionBean  implements Serializable
{
    private static final long serialVersionUID = 3358863832509265545L;

    private String name;
    private String gsid;
	private Object value;
	
	public String getName() {
    	return name;
    }
	public void setName(String name) {
    	this.name = name;
    }
	public Object getValue() {
    	return value;
    }
	public void setValue(Object value) {
    	this.value = value;
    }
	
	public Object getIdentifier()
	{
		return new SessionBeanKey(gsid,name);
	}
	
	public void setIdentifier(Object o)
	{
		SessionBeanKey key = (SessionBeanKey) o;
		this.gsid = key.gsid;
		this.name = key.name;
	}
	public void setData(byte[] b)
		throws Exception
	{
		ByteArrayInputStream bo = new ByteArrayInputStream(b);
		ObjectInputStream in = new ObjectInputStream(bo);
		value = in.readObject();
	}
	public byte[] getData() 
		throws IOException
	{
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bo);
		out.writeObject(value);
		out.flush();
		return bo.toByteArray();
	}
	public String getGsid()
    {
    	return gsid;
    }
	public void setGsid(String gsid)
    {
    	this.gsid = gsid;
    }
	@Override
	public int hashCode()
	{
		return gsid.hashCode() + name.hashCode() + value.hashCode();
	}
	@Override
	public boolean equals(Object o)
	{
		boolean result = false;
		if(o instanceof SessionBeanKey)
		{
			SessionBean lhs = (SessionBean)o;
			result = gsid.equals(lhs.gsid) && name.equals(lhs.name)
			 && value.equals(lhs.value);
		}
		return result;
	}

}
