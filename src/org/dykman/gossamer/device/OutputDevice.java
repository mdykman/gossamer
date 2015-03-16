package org.dykman.gossamer.device;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface OutputDevice
{
	public void setOutputStream(OutputStream outputStream);
	public void execute(HttpServletRequest request, HttpServletResponse response, byte[] content);
}
