package org.dykman.gossamer.log;

public interface LogManager
{
	public void writeLog(String file,String message);
	public void writeLog(String file,String message,Throwable e);
}
