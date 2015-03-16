package org.dykman.gossamer.location;

import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;

public class LocationServiceWrapper {
	private LookupService cl;
	public LocationServiceWrapper () {}
	public void setDataFile(String datafile){
//System.out.println("setDataFile: " + datafile);
		try {
			cl = new LookupService(datafile, LookupService.GEOIP_MEMORY_CACHE );
		} catch(java.io.IOException e) {
			throw new RuntimeException(e);
		}
	}
	public synchronized Location getLocation(String ip) {
System.out.println("getLocation: " + ip + ", cp is " + (ip == null ? "null" : "ok"));
		return cl.getLocation(ip);
	}
}
