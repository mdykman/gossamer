package org.dykman.gossamer.xsl;

public class XslDeviceProfile
{
	private boolean clientTranformer;
	private String xslPath;
	
	public boolean isClientTranformer()
    {
    	return clientTranformer;
    }
	public void setClientTranformer(boolean clientTranformer)
    {
    	this.clientTranformer = clientTranformer;
    }
	public String getXslPath()
    {
    	return xslPath;
    }
	public void setXslPath(String xslPath)
    {
    	this.xslPath = xslPath;
    }
}
