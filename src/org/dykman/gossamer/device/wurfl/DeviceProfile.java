package org.dykman.gossamer.device.wurfl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.dykman.gossamer.core.GossamerException;
import org.dykman.gossamer.device.ClientDeviceProfile;
import org.dykman.gossamer.jdbc.JdbcManager;
import org.springframework.context.ApplicationContext;

class DeviceProfile implements ClientDeviceProfile {
	private ApplicationContext applicationContext;
	String connectionId;
	Integer did = null;
	Map<String, String> attr;
	boolean cache = true;
	public static String NOT_A_VALUE = "NOT_A_VALUE";

	public DeviceProfile(ApplicationContext applicationContext,
			String connectionId, int did, boolean cache) {
		this.applicationContext = applicationContext;
		this.connectionId = connectionId;
		this.did = did;
		this.attr = Collections.synchronizedMap(new HashMap<String, String>());

	}
	public DeviceProfile(ApplicationContext applicationContext,
			String connectionId, int did) {
		this(applicationContext,connectionId,did,true);
	}
	
	public void setCache(boolean b) {
		cache = b;
	}

	private Connection getConnection() throws SQLException {
		return ((JdbcManager) applicationContext.getBean("jdbcManager"))
				.get(connectionId);
	}

	public int getId() {
		return did;
	}

	public boolean getBoolean(String key) {
		String a = getAttribute(key);
		return a == null ? false : a.equalsIgnoreCase("true") ? true : false;
	}

	public String getAttribute(String key) {
		Connection conn = null;
		String result = null;
		PreparedStatement st1 = null;
		PreparedStatement st2 = null;
		if(cache) result = attr.get(key);
		if(result != null) {
			if(result == NOT_A_VALUE) result = null;
			return result;
		} 
		
		try {
			int cdid = did;
			conn = getConnection();
			st1 = conn.prepareStatement("SELECT val from attributes where did = ? and name = ?");
			st1.setString(2,key);

			st2 = conn.prepareStatement("SELECT fallback_id from devices where did = ?");
			while(result == null) {
				st1.setInt(1, cdid);
				ResultSet rs = st1.executeQuery();
				if(rs.next()) {
					result = rs.getString(1);
				} else {
					st2.setInt(1, cdid);
					ResultSet rs2 = st2.executeQuery();
					if(rs2.next()) {
						cdid = rs2.getInt(1);
					} else {
						break;
					}
					rs2.close();
				}
				rs.close();
			}
		} catch(Exception e) {
			throw new GossamerException(e);
		} finally {
			try {
				if(st1 != null) st1.close();
				if(st2 != null) st2.close();
			} catch(SQLException e) {
				// secondary: ignore
			}
		}
		if(result == null) attr.put(key, NOT_A_VALUE);
		else attr.put(key, result);
		return result;
	}
}
