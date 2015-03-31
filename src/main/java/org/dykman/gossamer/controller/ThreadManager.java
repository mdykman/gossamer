package org.dykman.gossamer.controller;


public interface ThreadManager
{
	public void execute(Runnable task);
	public void execute(Runnable[] tasks);
}
