package org.dykman.gossamer.device.wurfl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.dykman.gossamer.core.GossamerException;
import org.dykman.gossamer.device.ClientDeviceProfile;
import org.dykman.gossamer.device.DeviceProfileFactory;
import org.dykman.gossamer.jdbc.JdbcManager;
import org.dykman.gossamer.log.LogManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


public class JdbcProfileFactory implements ApplicationContextAware, DeviceProfileFactory {
 //   static final String	              NOT_A_VALUE	   = "NOT_A_VALUE";

	String connectionId ;
	Integer defaultDevice;
	ApplicationContext applicationContext;
	JdbcManager jdbcManager;
	
	public void setConnectionId(String id) {
		this.connectionId = id;
	}
	public void setDefaultDevice(int defaultDevice)
	{
		this.defaultDevice = defaultDevice;
	}



	public String getFallbackFormat(String format) {
		String result = null;
		try {
			Connection connection = getJdbcManager().get(connectionId);
			PreparedStatement stat = connection.prepareStatement(
				"select fallback from formats where format = ?");
			stat.setString(1, format);
			ResultSet rs = stat.executeQuery();
			if(rs.next()) {
				result = rs.getString(1);
			}
			rs.close();
			stat.close();
			
		} catch(SQLException e) {
			throw new GossamerException(e);
		}
		return result;
	}
	
	public String getFallbackStyle(String style) {
		String result = null;
		try {
			Connection connection = getJdbcManager().get(connectionId);
			PreparedStatement stat = connection.prepareStatement(
				"select fallback from styles where style = ?");
			stat.setString(1, style);
			ResultSet rs = stat.executeQuery();
			if(rs.next()) {
				result = rs.getString(1);
			}
			rs.close();
			stat.close();
			
		} catch(SQLException e) {
			throw new GossamerException(e);
		}
		return result;
	}

	
	private synchronized JdbcManager getJdbcManager() {
		if(jdbcManager == null) {
			jdbcManager = (JdbcManager) applicationContext.getBean("jdbcManager");
		}
		return jdbcManager;
	}
	private Integer resolveByLabel( String label)
	{
		Integer deviceid = null;

		try {
			Connection connection = getJdbcManager().get(connectionId);
			PreparedStatement lookupSt = connection.prepareStatement("select did from devices where device_id like ?");
			lookupSt.setString(1, label);
			ResultSet rs = lookupSt.executeQuery();
			if(rs.next()) {
				deviceid = rs.getInt(1);
			}
			rs.close();
			lookupSt.close();
		} catch(SQLException e) {
			LogManager logManager = (LogManager) applicationContext.getBean("logManager");
			logManager.writeLog("device.log", "SQL error resolving device " + e.getMessage());
		}
		return deviceid;

	}

	private Integer resolveDevice(String userAgent)
	{
		Integer deviceid = null;

		if ( userAgent.startsWith("Vodafone/")) {
			 return resolveDevice(userAgent.substring(9,userAgent.length()));
		}

		try {
			Connection connection = getJdbcManager().get(connectionId);
			PreparedStatement st = connection.prepareStatement(
				"select did, user_agent FROM devices WHERE user_agent = ?");
			st.setString(1, userAgent);
			ResultSet rs = st.executeQuery();
			if(rs.next()) {
				deviceid = rs.getInt(1);
			}
			rs.close();
			st.close();
			st = connection.prepareStatement(
// Sqlite syntax
					"select did, user_agent from devices WHERE user_agent LIKE (? || '%')"
							+ " ORDER BY length(user_agent) DESC");
//MySQL syntax
//			"select did, user_agent from devices WHERE user_agent LIKE concat(?,'%')"
//			+ " ORDER BY length(user_agent) DESC");
//			boolean hit = false;
			int n = userAgent.length();
			while(n > 4) {
				n *= 0.7;
				st.setString(1, userAgent.substring(0, n));
				rs = st.executeQuery();
				if(rs.next()) {
//					hit = true;
					deviceid = rs.getInt(1);
				}
				rs.close();
			}
			st.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		if(deviceid == null)
		{
			if (userAgent.indexOf("UP.Browser/7") != -1 ) {
			    deviceid = resolveByLabel("opwv_v7_generic");
			}
			else if (userAgent.indexOf("UP.Browser/6") != -1 ) {
				deviceid = resolveByLabel("opwv_v6_generic");
			}
			else if (userAgent.indexOf("UP.Browser/5") != -1 ) {
				deviceid = resolveByLabel("upgui_generic");
			}
			else if (userAgent.indexOf("UP.Browser/4") != -1 ) {
				deviceid = resolveByLabel("uptext_generic");
			}
			else if (userAgent.indexOf("Series60") != -1 ) {
				deviceid = resolveByLabel("nokia_generic_series60");
			}
			//web browsers?
			else if (userAgent.indexOf("Mozilla/4.0") != -1 ) {
				deviceid = resolveByLabel("generic_web_browser");
			}
			else if (userAgent.indexOf("Mozilla/5.0") != -1 ) {
				deviceid = resolveByLabel("generic_web_browser");
			}
			else if (userAgent.indexOf("Mozilla/6.0") != -1 ) {
				deviceid = resolveByLabel("generic_web_browser");
			}				
		}
		return deviceid;
	}

	public ClientDeviceProfile getDeviceProfileByLabel(String label)
	{
// System.out.println("seaching for device by tag: " + label);
		Integer deviceid = resolveByLabel(label);
		if (deviceid != null) {
			return new DeviceProfile(applicationContext,connectionId,deviceid);
		} 
		else {
			return null;
		}
	}

	public ClientDeviceProfile getDeviceProfile(int deviceid) {
		return new DeviceProfile(applicationContext,connectionId,deviceid);
	}

	public ClientDeviceProfile getDeviceProfile(String userAgent)
	{
		Integer deviceid = null;
		if (userAgent == null)
			deviceid = defaultDevice;

		if (deviceid == null) {
			if(userAgent != null) {
				deviceid = resolveDevice(userAgent);
			}
			if (deviceid == null)
				deviceid = defaultDevice;
		}

		return new DeviceProfile(applicationContext,connectionId,deviceid);
	}
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
}
