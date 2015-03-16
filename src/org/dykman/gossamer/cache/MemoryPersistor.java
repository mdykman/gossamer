package org.dykman.gossamer.cache;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class MemoryPersistor implements Persistor
{
	long ttl = 60000;
	int size= 1024;
	Map<Object, Entry> map;
	
	protected long getTime() {
		return System.currentTimeMillis();
	}
	
	public MemoryPersistor() {
		map = (Map <Object, Entry>)Collections.synchronizedMap(new MyMap());
	}

	public void setTtl(long t) {
		ttl = t;
	}

	public Object get(Object key) {
		Entry ee = map.get(key);
		if(getTime() <= ee.expires) {
			return ee.data;
		}
		return null;
	}

	public void set(Object key, Object value) {
		set(key, value, ttl);
	}
	
	public void set(Object key, Object value,long ttl) {
		Entry ee = new Entry(ttl == 0 ? 0 : key,getTime() + ttl);
				map.put(key, ee);
	}
	
	class MyMap extends LinkedHashMap<Object, Entry> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5887465452065754578L;

		@Override
	    protected boolean removeEldestEntry(Map.Entry<Object,Entry> eldest) {
			boolean result = false;
			if(eldest.getValue().expires != 0) {
				if(size<=this.size()) {
					this.remove(eldest.getKey());
					result = true;
				}
			} else {
				this.put(eldest.getKey(), eldest.getValue());
			}
	        return result;
	    }
	}

	class Entry {
		Object data;
		long expires;
		
		Entry(Object d,long e) {
			data = d;
			expires = e;
		}
	}

	public void setSize(int s) {
    	this.size = s;
    }
}
