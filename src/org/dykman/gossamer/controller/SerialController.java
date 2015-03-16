package org.dykman.gossamer.controller;

import java.util.List;


public class SerialController implements Controller
{

	public void invoke(List<Runnable> runnables)
	{
		for(Runnable runnable : runnables) {
			try {
				runnable.run();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}
