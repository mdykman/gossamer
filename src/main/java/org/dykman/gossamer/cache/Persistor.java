package org.dykman.gossamer.cache;

public interface Persistor
{
	public void set(Object key,Object value, long ttl);
	public void set(Object key,Object value);
	public Object get(Object key);
}
