package org.dykman.gossamer.webapp.cookie;

public interface CookieManager
{
	public String getCookie(String name);

  	public void setCookie(String name,String value);
 
	public void setCookie(String name,String value, int expiry);
	public void set(String name,String value);
	public String get(String name);
}
