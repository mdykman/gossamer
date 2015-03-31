package org.dykman.gossamer.upload;

import java.io.InputStream;

public interface InputPersister
{
	public void save(String key,InputStream input);
}
