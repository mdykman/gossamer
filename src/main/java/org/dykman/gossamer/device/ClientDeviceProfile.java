package org.dykman.gossamer.device;

public interface ClientDeviceProfile
{
	public int getId();
	public String getAttribute(String name);
	public boolean getBoolean(String name);
}
