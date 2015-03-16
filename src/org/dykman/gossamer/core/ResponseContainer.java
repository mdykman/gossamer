package org.dykman.gossamer.core;

public interface ResponseContainer
{
	public void enqueue(String component, String method, Object o);
	public ResponseBean[] getResponse();
}
