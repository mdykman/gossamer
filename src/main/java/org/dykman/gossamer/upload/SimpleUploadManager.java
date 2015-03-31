package org.dykman.gossamer.upload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.dykman.gossamer.core.GossamerException;
import org.dykman.gossamer.log.LogManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SimpleUploadManager implements UploadManager, ApplicationContextAware
{
	static Random random;
	static {
		random = new Random();
	}
	
	private String tempDirectory;
	private ApplicationContext applicationContext;

	private Map<String,FileUpload> files = new HashMap<String,FileUpload>();
	private List<String> filePaths = new ArrayList<String>();

	public String[] getUploads() {
		return files.keySet().toArray(new String[files.size()]);
	}
	public void setTempDirectory(String tempDirectory)
    {
    	this.tempDirectory = tempDirectory;
    }

	public void setApplicationContext(ApplicationContext applicationContext)
   {
    	this.applicationContext = applicationContext;
   }

	protected String normalizeName(String s)
	{
		return s.replaceAll("[^-a-zA-Z0-9.]", "_");
	}
	
	protected String translate(String s)
	{
		byte[] bb = new byte[4];
		synchronized(random) {
			random.nextBytes(bb);
		}
		File f = new File(tempDirectory,toHexString(bb) +
				"-" + normalizeName(s));
		return f.getAbsolutePath();
	}

	/**
	 * 
	 * @param key the key value return from persist
	 * @return a description of a pending upload
	 */
	public FileUpload getUpload(String key)
	{
		return files.get(key);
	}
	
	/**
	 * returns a key to pass to getUpload
	 * @param item a FileITem from the apache-commons-fileupad module
	 */
	long lastSize = 0;
	
	protected String toHexString(byte[] bb)
	{
		StringBuffer buffer = new StringBuffer();
		for(byte b : bb) {
			int n = 0xff & (int)b;
			if( n < 16) buffer.append('0');
			buffer.append(Integer.toHexString(n));
		}
		return buffer.toString();
	}
	
	public String persist(HttpServletRequest req, FileItem item)
	{
//		Calendar now = Calendar.getInstance();
		LogManager logManager = (LogManager) applicationContext.getBean("logManager");

		if(!item.isFormField())
		{
			try
			{
				String name = normalizeName(item.getName());
				logManager.writeLog("uploads.log", "receiving file " + 
					item.getName() + " as " + name);
				String path = translate(name);
				String contentType = item.getContentType();
				byte[] sha = save(item.getInputStream(),path,"MD5");
				String hash = toHexString(sha);
				long size = lastSize;

				String remoteAddress = req.getHeader("X-Forwarded-For");
				if(remoteAddress == null)
					remoteAddress = req.getHeader("X-Originating-IP");
				if(remoteAddress == null)
					remoteAddress = req.getHeader("X-Via");
				if(remoteAddress == null)
					remoteAddress = req.getRemoteAddr();

				FileUpload fu = new FileUpload();

				fu.setMimeType(contentType);
				fu.setOriginName(name);
				fu.setSize(size);
				fu.setStoragePath(path);
				fu.setRemoteAddress(remoteAddress);
				fu.setSecureHash(hash);

				
				files.put(path, fu);
				filePaths.add(path);

				return path;
			}
			catch(Exception e)
			{
				throw new GossamerException(e);
			}
		}
		return null;
	}
	

	protected byte[] save(InputStream in, String path,String algo)
	{
		try
		{
			File file = new File(path);
			MessageDigest digest = MessageDigest.getInstance(algo);
			OutputStream out = new FileOutputStream(file);
			byte bb[] = new byte[4096];
			int n;
			int count = 0;
			while((n = in.read(bb)) != -1)
			{
				out.write(bb, 0, n);
				digest.update(bb, 0, n);
				count+=n;
			}
			lastSize = count;
			out.close();
			return digest.digest();
		}
		catch(Exception e)
		{
			throw new GossamerException(e);
		}
	}

	public void destroy()
	{
		for(String s : filePaths)
		{
			File f = new File(s);
			if(f.exists())
				f.delete();
		}
	}
	
}
