package org.dykman.gossamer.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

public class JdbcManager
{
	private Map<String, DataSource> sources;
	private Map<String, Connection> connections
	 = new HashMap<String, Connection>();;
	public JdbcManager() {}
	
	public void setSources(Map<String, DataSource> sources) {
    	this.sources = sources;
    }
	
	public Connection get(String name)
		throws SQLException
	{
		Connection c = connections.get(name);
		if(c == null) {
			DataSource ds = sources.get(name);
			if(ds != null) {
				c = ds.getConnection();
				connections.put(name, c);
			}
		}
		return c;
	}
	
	public void closeAll() {
		for(Connection c : connections.values()) {
			try {
				c.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}
