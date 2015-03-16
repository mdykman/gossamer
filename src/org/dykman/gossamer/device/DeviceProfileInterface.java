package org.dykman.gossamer.device;

import org.hibernate.Session;

public class DeviceProfileInterface
{
	Session session;
	DeviceProfileFactory factory;
	
	public void setSession(Session session)
    {
    	this.session = session;
    }
	public void setDeviceProfileFactory(DeviceProfileFactory factory)
    {
    	this.factory = factory;
    }
	public ClientDeviceProfile getDeviceProfile(String userAgent)
	{
		return null;
	}
}
