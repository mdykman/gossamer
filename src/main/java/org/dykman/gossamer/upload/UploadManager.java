package org.dykman.gossamer.upload;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;

public interface UploadManager
{
	public String persist(HttpServletRequest req, FileItem item);
	public FileUpload getUpload(String key);
	public String[] getUploads();
}
