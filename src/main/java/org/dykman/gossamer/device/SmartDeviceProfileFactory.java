package org.dykman.gossamer.device;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dykman.gossamer.log.LogManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SmartDeviceProfileFactory 
	implements DeviceProfileFactory, ApplicationContextAware
{
	
	public static final int DEFAULT_DEVICE = 6578;
	ApplicationContext applicationContext;
	private static Pattern firefox, msie, winCE, chrome, safari, iphone, blackberry;
	private Map<String, ClientDeviceProfile> deviceMap = new java.util.concurrent.ConcurrentHashMap<String, ClientDeviceProfile>();

	private String fallBackFactoryId;
	
	private boolean cacheDevices = false;
	@SuppressWarnings("unused")
	private boolean cacheAttributes = false;
	
	
	protected DeviceProfileFactory getFallBackFactory() {
		return (DeviceProfileFactory)
		applicationContext.getBean(fallBackFactoryId);
		
	}
	

	static {
		firefox = Pattern.compile(".*?(Fire[Ff]ox)/([\\d]+([.][\\d]+)*).*");
		chrome = Pattern.compile(".*?(Chrome)/([0-9]+([.][0-9]+)*).*");
		iphone = Pattern.compile(".*?(iP(ad|hone)).*?OS ([\\d](_[\\d]*)).*");
		blackberry = Pattern.compile(".*?(BlackBerry)([\\d]*/[\\d]+([.][\\d]+)*).*");
		msie = Pattern.compile(".*?(MSIE) ([\\d]+([.][\\d]+)*).*");
		safari = Pattern.compile(".*?Version/([\\d]+([.][\\d])*) (Safari)/[\\d]+([.][\\d])*.*");
		winCE = Pattern.compile(".*?(Windows CE).*",Pattern.CASE_INSENSITIVE);
	}

	public ClientDeviceProfile getDeviceProfile(String userAgent) {
		
		DeviceProfileFactory fallbackFactory = getFallBackFactory();
		LogManager logManager = (LogManager) applicationContext.getBean("logManager");

		if(userAgent == null) {
			logManager.writeLog("device.log", "using default device " + DEFAULT_DEVICE + "for null user agent");
			return fallbackFactory.getDeviceProfile(DEFAULT_DEVICE);
			
		}
		String vendor = null, version = null;
		ClientDeviceProfile profile = null;
		if (cacheDevices) {
			profile = deviceMap.get(userAgent);
			if(profile != null) {
				return profile;
			}
		}

		if(userAgent != null) {
			Matcher matcher = firefox.matcher(userAgent);
			if(matcher.matches()) {
				vendor = matcher.group(1);
				version = matcher.group(2);
			}
			if(vendor == null) {
				matcher = chrome.matcher(userAgent);
				if(matcher.matches()) {
					vendor = matcher.group(1);
					version = matcher.group(2);
				}
			}
			if(vendor == null) {
				matcher = safari.matcher(userAgent);
				if(matcher.matches()) {
					vendor = matcher.group(3);
					version = matcher.group(1);
				}
			}
			
			if(vendor == null) {
				matcher = iphone.matcher(userAgent);
				if(matcher.matches()) {
					vendor = matcher.group(1);
					version = matcher.group(2).replaceAll("_",".");
				}
			}
			if(vendor == null) {
				matcher = blackberry.matcher(userAgent);
				if(matcher.matches()) {
					vendor = matcher.group(1);
					version = matcher.group(2).replaceAll("/",".");
				}
			}
			if(vendor == null) {
				// scan for ie
				matcher = msie.matcher(userAgent);
				if(matcher.matches()) {
					vendor = matcher.group(1);
					version = matcher.group(2);
					Matcher ce = winCE.matcher(userAgent);
					if(ce.matches()) {
						vendor = vendor + "-ce";
					}
				}
			}
		}
		if(vendor != null) {
			StringBuilder sb = new StringBuilder(vendor).append("/").append(version);
			profile = fallbackFactory.getDeviceProfileByLabel(sb.toString());
			int n = sb.length();
			while(profile == null && (n = sb.lastIndexOf(".",n)) != -1) {
				sb.replace(n,sb.length(),"");
				profile = fallbackFactory.getDeviceProfileByLabel(sb.toString());
			}
			if(profile == null) profile = fallbackFactory.getDeviceProfileByLabel(vendor);
			
		}
		if(profile == null) {
			// TODO:: this should be a property
			profile = fallbackFactory.getDeviceProfile(DEFAULT_DEVICE);
		}
		if(profile == null) profile = fallbackFactory.getDeviceProfile(userAgent);

		if(profile == null) {
			logManager.writeLog("device.log", "failed to find a device for user agent " + userAgent);
		} else  { 
			if(cacheDevices) deviceMap.put(userAgent,profile);
			logManager.writeLog("device.log", "found device " + profile.getId() + " for user agent " + userAgent);
		}

		return profile;
	}
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	public SmartDeviceProfileFactory() {
		
	}
	public ClientDeviceProfile getDeviceProfile(int did) {
		DeviceProfileFactory fallbackFactory = getFallBackFactory();
		return fallbackFactory.getDeviceProfile(did);
	}
	public ClientDeviceProfile getDeviceProfileByLabel(String label) {
		DeviceProfileFactory fallbackFactory = getFallBackFactory();
		return fallbackFactory.getDeviceProfileByLabel(label);
	}
	public void setCacheDevices(boolean cacheDevices) {
		this.cacheDevices = cacheDevices;
	}
	public void setCacheAttributes(boolean cacheAttributes) {
		this.cacheAttributes = cacheAttributes;
	}
	public void setFallBackFactoryId(String fallBackFactoryId) {
		this.fallBackFactoryId = fallBackFactoryId;
	}
	
}
