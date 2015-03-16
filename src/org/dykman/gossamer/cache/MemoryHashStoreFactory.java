package org.dykman.gossamer.cache;

import java.util.HashMap;

public class MemoryHashStoreFactory implements HashStoreFactory
{
	java.util.Map<String,Object> map = new HashMap<String, Object>();
	long ttl;
	
	
	public MemoryHashStoreFactory()
	{
	}

	public Persistor getPersistor()
	{
		return null;
	}

	public Persistor getPersistor(long ttl)
	{
		return null;
	}
	
	class Entry {
		Object data;
		long expires;
	}

	public void setTtl(long ttl)
    {
    	this.ttl = ttl;
    }

}
