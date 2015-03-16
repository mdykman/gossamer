package org.dykman.gossamer.handler;

import java.util.Map;

public class GreetingHandler
{
	public Object hello(Map<String, String> params)
	{
		return "hi there";
	}
	public Object goodbye(Map<String, String> params)
	{
		return "see you later";
	}
}
