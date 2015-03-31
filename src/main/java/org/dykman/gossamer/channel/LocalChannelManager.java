package org.dykman.gossamer.channel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dykman.gossamer.core.PageController;
import org.dykman.gossamer.core.ResponseBean;
import org.dykman.gossamer.util.ArrayUtils;

public class LocalChannelManager implements ChannelManager
{
	Map<String, Channel>	channels	= new HashMap<String, Channel>();

	public void removeFromAllChannels(PageController controller)
	{
		synchronized (channels)
		{
			Iterator<Map.Entry<String, Channel>> it = channels.entrySet()
			        .iterator();
			Map.Entry<String, Channel> en;
			while (it.hasNext())
			{
				en = it.next();
				if (!removeListener(en.getKey(), controller, false))
				{
					it.remove();
				}
			}
		}
	}

	public boolean createChannel(String name)
	{
		boolean result = false;
		synchronized (channels)
		{
			if (!channels.containsKey(name))
			{
				Channel ch = new Channel(false);
				channels.put(name, ch);
				result = true;
			}
		}
		return result;
	}

	public void removeListener(String channel, PageController controller)
	{
		removeListener(channel, controller, true);
	}

	public boolean removeListener(String channel, PageController controller,
	        boolean remove)
	{
		boolean result = true;
		synchronized (channels)
		{
			Channel ss = channels.get(channel);
			if (ss != null)
			{
				ss.remove(controller);
				if (ss.isAnonymous() && ss.isEmpty())
				{
					result = false;
					if (remove)
						channels.remove(channel);
				}
			}
		}
		return result;
	}

	public void addListener(String channel,
	        org.dykman.gossamer.core.PageController controller)
	{
		synchronized (channels)
		{
			Channel ss = channels.get(channel);
			if (ss == null)
			{
				ss = new Channel();
				channels.put(channel, ss);
			}
			ss.add(controller);
		}
	}

	public void broadcast(String channel, ResponseBean response)
	{
		Channel ss = channels.get(channel);
		if (ss != null)
		{
			synchronized (ss)
			{
				ss.broadcast(response);
			}
		}
	}

	public void setCoreChannels(String s)
	{
		String[] cc = ArrayUtils.fromStringBlock(s);
		for (String c : cc)
		{
			createChannel(c);
		}
	}
}
