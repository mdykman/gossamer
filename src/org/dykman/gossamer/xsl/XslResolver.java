package org.dykman.gossamer.xsl;

import org.dykman.gossamer.device.ClientDeviceProfile;

public interface XslResolver
{
	public void setFormat(String format);
	public String getFormat();
	public String resolveXslPath(ClientDeviceProfile deviceProfle,  String view);
	public String resolveXslUrl(ClientDeviceProfile deviceProfle,  String view);
}
