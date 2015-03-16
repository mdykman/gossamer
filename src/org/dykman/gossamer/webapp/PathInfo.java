package org.dykman.gossamer.webapp;

public class PathInfo
{
	String handlerPathInfo;
	boolean trim = false;

	public String getPathInfo() {
		return handlerPathInfo;
	}
	public String getHandlerPathInfo()
    {
	 	String s = handlerPathInfo;
		if(trim && s != null && s.startsWith("/")) {
			s = s.substring(1);
		}
    	return s;
    }

	public void setTrim(boolean b) {
		trim = b;
	}
	public void setHandlerPathInfo(String handlerPathInfo)
    {
    	this.handlerPathInfo = handlerPathInfo;
    }
}
