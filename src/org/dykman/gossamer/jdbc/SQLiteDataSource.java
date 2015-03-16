package org.dykman.gossamer.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

public class SQLiteDataSource implements DataSource {

	String database;
	int loginTimeout;
	PrintWriter logWriter;;

	public SQLiteDataSource() {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	public void setDatabase(String db) {
		database=db;
	}
	
	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return logWriter;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		logWriter = out;
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		loginTimeout = seconds;
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return loginTimeout;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:sqlite:" + database);
	}

	@Override
	public Connection getConnection(String username, String password)
			throws SQLException {
		return getConnection();
	}
	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return Logger.getLogger("org.dykman.gossamer");
	}

}
