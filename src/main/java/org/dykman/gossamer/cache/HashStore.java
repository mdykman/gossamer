package org.dykman.gossamer.cache;

public interface HashStore
{
	public boolean isTransient();
	public boolean acceptsKey();
	
	public boolean set(Object key,Object value);
	public Object get(Object key);
}
