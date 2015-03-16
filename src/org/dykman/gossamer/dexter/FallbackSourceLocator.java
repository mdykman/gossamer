package org.dykman.gossamer.dexter;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.dykman.gossamer.core.GossamerException;
import org.dykman.gossamer.core.SourceLocator;
import org.dykman.gossamer.jdbc.JdbcManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class FallbackSourceLocator implements SourceLocator, ApplicationContextAware {
	static final int DEVICE_SEARCH = 1;
	static final int STYLE_SEARCH = 2;

	JdbcManager jdbcManager;
	String connectionId;
	String resourceRoot;
	
	ApplicationContext applicationContext;
	PreparedStatement stat = null;

	public String toWebPath(File f) {
		String result = null;
		String abs = f.getAbsolutePath();
		if(abs.startsWith(resourceRoot)) {
			result  = "/xsl" + abs.substring(resourceRoot.length());
		}
		return result;
	}
	public String formOutputName(File source) {
		Long lm = source.lastModified();
		String hash = Long.toHexString(lm);
		StringBuilder sb = new StringBuilder();

		String wp = toWebPath(source);
		int n = wp.lastIndexOf('.');
		if (n == -1) {
			sb.append(wp).append("$").append(hash);
		} else {
			sb.append(wp.substring(0, n)).append('$').append(hash)
					.append(wp.substring(n));
		}
		sb.append(".xsl");
		return sb.toString();
	}
	@Override
	public File locateSource(String source) {
		jdbcManager = getJdbcManager();

		File sourceFile = new File(resourceRoot,source);
		if(sourceFile.exists()) return sourceFile;
		
		Connection conn = null;
		try {
			conn = jdbcManager.get(connectionId);
			if(conn ==null) {
				throw new GossamerException(connectionId 
						+ " does not appear to be a valid JDBC connection.");
			}
			stat = conn.prepareStatement(
					"SELECT fallback from fallbacks where name = ? and ftype = ?");
		} catch(SQLException e) {
			throw new GossamerException(e);
		}
	
		String ss = source;
		try {
			while(!sourceFile.exists()) {
				while(!sourceFile.exists()) {
					ss = composeFallBack(ss,DEVICE_SEARCH);
					if(ss == null) break;
						sourceFile =  new File(resourceRoot,ss);
				}
				if(!sourceFile.exists()) {
					ss = source = composeFallBack(source,STYLE_SEARCH);
					if(source == null) break;
					sourceFile =  new File(resourceRoot,ss);
				}
			}
		} finally {
			try {
				if(stat != null) stat.close();
			} catch(SQLException e) {
				System.err.println("error closing statement: " + e.getLocalizedMessage());
			}
		}
	
		if(sourceFile.exists()) return sourceFile;
		else return null;

	}

	// TODO:: must reimplement for db
	
	protected JdbcManager getJdbcManager() {
		if(jdbcManager == null) {
			jdbcManager = (JdbcManager) applicationContext.getBean("jdbcManager");
		}
		return jdbcManager;
		
	}
	private String findFallBack(String param,int type) {
		jdbcManager = getJdbcManager();

		String result = null;
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet rs = null;
		try {
			conn = jdbcManager.get(connectionId);
			stat = conn.prepareStatement(
					"SELECT fallback from fallbacks where name = ? and ftype = ?");
			stat.setString(1, param);
			stat.setInt(2, type);
			rs = stat.executeQuery();
			if(rs.next()) {
				result = rs.getString(1);
			}
		} catch(SQLException e) {
			throw new GossamerException(e);
		} finally {
			try {
				if(rs != null) rs.close();
			} catch(Exception e)  {
				System.err.println("error while closing statement: " + e.getLocalizedMessage());
			}
		}
		return result;
	}

	protected String composeFallBack(String path, int type) {
		String[] parts = path.split("[/]", 3);
		if (parts.length == 3) {
			String fb = findFallBack(parts[type == 1 ? 1 : 0], type);
			if (fb != null) {
				StringBuilder sb = new StringBuilder();
				if (type == 1) {
					sb.append(parts[0]).append('/').append(fb).append('/')
							.append(parts[2]);
				} else {
					sb.append(fb).append('/').append(parts[1]).append('/')
							.append(parts[2]);
				}
				return sb.toString();
			}
		}
		return null;
	}

	public String guessSource(String s) {
		if (s.endsWith(".xsl")) {
			s = s.substring(0, s.length() - 4);
			int n = s.lastIndexOf('-');
			int o = s.lastIndexOf('.');
			if (n > o) {
				// looks like a sub-template
				int p = s.indexOf('-', o);
				s = s.substring(0, p);
			}
			// extract the hash, if it's there
			n = s.lastIndexOf('$');
			if (n != -1) {
				o = s.indexOf('.', n);
				if (o != -1) {
					s = s.substring(0, n) + s.substring(o);
				}
			}
		}

		return s;
	}

	public String getResourceRoot() {
		return resourceRoot;
	}

	public void setResourceRoot(String resourceRoot) {
		this.resourceRoot = resourceRoot;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public String getConnectionId() {
		return connectionId;
	}

	public void setConnectionId(String connectionId) {
		this.connectionId = connectionId;
	}

}
