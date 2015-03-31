package org.dykman.gossamer.cache;

public interface HashStoreFactory
{
	Persistor getPersistor();
	Persistor getPersistor(long ttl);
}
