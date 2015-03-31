package org.dykman.gossamer.upload;

import java.util.Calendar;

public class FileUpload
{
	private int uploadID = -1;
	private long size;
	private Calendar uploadTime;
	private String originName;
	private String storagePath;
	private String mimeType;
	private String secureHash;
	private String remoteAddress;


	public int getUploadID()
    {
    	return uploadID;
    }
	public void setUploadID(int uploadID)
    {
    	this.uploadID = uploadID;
    }
	public Calendar getUploadTime()
    {
    	return uploadTime;
    }
	public void setUploadTime(Calendar uploadTime)
    {
    	this.uploadTime = uploadTime;
    }
	public String getOriginName()
    {
    	return originName;
    }
	public void setOriginName(String originName)
    {
    	this.originName = originName;
    }
	public String getStoragePath()
    {
    	return storagePath;
    }
	public void setStoragePath(String storagePath)
    {
    	this.storagePath = storagePath;
    }
	public String getSecureHash()
    {
    	return secureHash;
    }
	public void setSecureHash(String secureHash)
    {
    	this.secureHash = secureHash;
    }
	public String getRemoteAddress()
    {
    	return remoteAddress;
    }
	public void setRemoteAddress(String remoteAddress)
    {
    	this.remoteAddress = remoteAddress;
    }
	public String getMimeType()
    {
    	return mimeType;
    }
	public void setMimeType(String mimeType)
    {
    	this.mimeType = mimeType;
    }
	public long getSize()
    {
    	return size;
    }
	public void setSize(long size)
    {
    	this.size = size;
    }
}

