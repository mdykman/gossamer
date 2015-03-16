package org.dykman.gossamer.jdbc;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.dykman.gossamer.log.LogManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class JdbcManagerFactory implements ApplicationContextAware {
	ApplicationContext applicationContext;
	private Map<String, DataSource> sources = null;

	public JdbcManager getJdbcManager() {
		if(sources == null) synchronized(this) {
			if(sources == null) {
				sources = new HashMap<String, DataSource>();
				String[] ss = applicationContext.getBeanNamesForType(DataSource.class);
				LogManager logManager = (LogManager) applicationContext.getBean("logManager");
				for(String s : ss) {
					String id = s;
					if(s.endsWith("DataSource")) {
						id = s.substring(0,s.lastIndexOf('D'));
					}
					logManager.writeLog("jdbc.log", "adding datasource `" + id + "'");
					DataSource src = (DataSource) applicationContext.getBean(s);
					sources.put(id,src);
				}
			}
		}
		JdbcManager j = new JdbcManager();
		j.setSources(sources);
		return j;
		
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

}
