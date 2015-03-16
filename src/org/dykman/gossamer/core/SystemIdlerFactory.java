package org.dykman.gossamer.core;

public class SystemIdlerFactory implements IdlerFactory
{

	public Idler createIdler()
	{
		return new SystemIdler();
	}

}
