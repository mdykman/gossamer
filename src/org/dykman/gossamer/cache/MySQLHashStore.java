package org.dykman.gossamer.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Map;

import org.dykman.gossamer.util.FileUtils;

public class MySQLHashStore {

	public static final String DEFAULT_TABLE_NAME = "gossamer_cache_store";
	Connection connection;
	String tableName;

	public MySQLHashStore(Connection connection, String tableName) {
		this.connection = connection;
		this.tableName = tableName;
	}

	public MySQLHashStore(Connection connection) {
		this(connection, DEFAULT_TABLE_NAME);
	}

//	@Override
	public boolean isTransient() {
		// TODO Auto-generated method stub
		return false;
	}

//	@Override
	public boolean acceptsKey() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public boolean set(Object key, Object value) {
		return set(key,value,"general");
	}
	
	public boolean set(Object okey, Object value, String type) {
		if (!(value instanceof Serializable)) {
			throw new RuntimeException(
					"object is not an instance of Serializable");
		}
		String key = createKey(okey);

		try {
			long now = System.currentTimeMillis();
			// serialize the value if you can
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(stream);
			out.writeObject(value);
			out.flush();
			PreparedStatement statement;
			statement = connection.prepareStatement("UPDATE " + tableName
					+ " SET val = ?, updated = ?, obj_type =  ? WHERE name = ?");
			statement.setBytes(1, stream.toByteArray());
			statement.setTimestamp(2, new Timestamp(now));
			statement.setString(3, type);
			statement.setString(4, key.toString());
			int n = statement.executeUpdate();
			statement.close();
			if (n == 0) {
				statement = connection.prepareStatement("INSERT INTO "
						+ tableName + " set name = ?, val = ?, obj_type = ?");
				statement.setString(1, key.toString());
				statement.setBytes(2, stream.toByteArray());
				statement.setString(3,type);
				statement.executeUpdate();
				statement.close();
			}
			// TODO Auto-generated method stub
		} catch (Exception e) {
			System.err.println("ERROR MESSAGE: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("object failed to store",e);
		}
		return false;
	}

//	@Override
	public Object get(Object okey) {
		String key = createKey(okey);
		Object result = null;
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement("SELECT val, updated from "
					+ tableName + " where name = ?");
			statement.setString(1, key.toString());
			ResultSet rs = statement.executeQuery();
			try {
				if (rs.next()) {
					
					long ts = rs.getTimestamp(2).getTime();
					long  now = System.currentTimeMillis();
					if((now - ts) > 86400000) {
						result = null;
					} else {
						byte[] bb = rs.getBytes(1);
						InputStream bbin = new ByteArrayInputStream(bb);
						ObjectInputStream in = new ObjectInputStream(bbin);
						result = in.readObject();
					}
				}
			} finally {
				rs.close();
			}
		} catch (Exception e) {
			throw new RuntimeException("error during fetch",e);
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
			}
		}
		return result;
	}

	public String createKey(Object key) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			createKey(key,digest);
			return FileUtils.toHex(digest.digest());
		} catch (Exception e) {
			throw new RuntimeException("illegal algorithm");
		}

	}

	@SuppressWarnings({ "unchecked" })
	public void createKey(Object key, MessageDigest digest) {
		byte[] item = "item".getBytes();
		try {
			if(key instanceof Map<?, ?>) {

				Map<?,?> map = ((Map<?,?>) key);
				for (Object kk : map.keySet()) {
					createKey(kk,digest);
					digest.digest(item);
					createKey(map.get(kk),digest);
					digest.digest(item);
				}
			} else if(key instanceof Collection) {
				Collection<?> set = (Collection<Object>) key;

				for (Object o : set) {
					digest.digest(item);
					createKey(o,digest);
				}
			} 
			else {
				digest.update(key.toString().getBytes());
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("illegal algorythm",e);
		}
	}
}
