package org.dykman.gossamer.controller;

import java.util.List;

public interface Controller
{
	public void invoke(List<Runnable> runnables);
}
