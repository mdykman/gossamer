package org.dykman.gossamer.channel;

import org.dykman.gossamer.core.PageController;
import org.dykman.gossamer.core.ResponseBean;

public interface ChannelManager
{
	public boolean createChannel(String name);
	public void addListener(String channel, PageController controller);
	public void removeListener(String channel, PageController controller);
	public void removeFromAllChannels(PageController controller);

	public void broadcast(String channel, ResponseBean response);
}
