package org.dykman.gossamer.webapp;

import java.io.OutputStream;

public interface Transformer
{
	public String getContentType();
	public void transform(byte[] in,OutputStream out);
}
