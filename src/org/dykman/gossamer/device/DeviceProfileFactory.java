package org.dykman.gossamer.device;

public interface DeviceProfileFactory
{
	public ClientDeviceProfile getDeviceProfileByLabel(String label);
	public ClientDeviceProfile getDeviceProfile(int deviceId);
	public ClientDeviceProfile getDeviceProfile(String userAgent);

}
