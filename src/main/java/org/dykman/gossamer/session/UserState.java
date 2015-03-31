package org.dykman.gossamer.session;

public interface UserState
{
	public void set(String s, Object o);
	public void remove(String string );
	public Object get(String string );
}
