package org.dykman.gossamer.device.wurfl;

public class Device
{
	private int did;
	private String deviceId;
	private boolean root;
	private String userAgent;
	private Integer fallbackId;
	
	public boolean isRoot()
    {
    	return root;
    }
	public void setRoot(boolean root)
    {
    	this.root = root;
    }
	public int getDid()
    {
    	return did;
    }
	public void setDid(int did)
    {
    	this.did = did;
    }
	public String getDeviceId()
    {
    	return deviceId;
    }
	public void setDeviceId(String deviceId)
    {
    	this.deviceId = deviceId;
    }
	public String getUserAgent()
    {
    	return userAgent;
    }
	public void setUserAgent(String userAgent)
    {
    	this.userAgent = userAgent;
    }
	public Integer getFallbackId()
    {
    	return fallbackId;
    }
	public void setFallbackId(Integer fallbackId)
    {
    	this.fallbackId = fallbackId;
    }
}
