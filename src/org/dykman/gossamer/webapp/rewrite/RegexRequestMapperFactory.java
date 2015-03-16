package org.dykman.gossamer.webapp.rewrite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.dykman.gossamer.core.GossamerException;
import org.dykman.gossamer.log.LogManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class RegexRequestMapperFactory implements ApplicationContextAware
{
	
	ApplicationContext applicationContext;
	public static final int RETURN = 0;
	public static final int START = 1;
	public static final int CONTINUE = 2;
	public static final int FILE = 3;
	
	long ruleStamp = 0L;

	private RegexpEntry[] rules;
	private File pathFile;
	private File resourceBase;
	
	public void setResourceBase(String basePath) {
		resourceBase = new File(basePath);
	}
	
	public void setRulePath(String path) {
		this.pathFile = new File(path);
	}

	public RequestMapper createRequestMapper() {
		RegexRequestMapper result = new RegexRequestMapper(this);
		result.setResourceBase(resourceBase);
log("setting resource path to " + resourceBase.getAbsolutePath());
		result.setRules(getRules());
		
		return result;
	}
	public RegexpEntry[] getRules() {
		try
		{
			if(pathFile.lastModified() != ruleStamp) {
				synchronized(this) {
					if(pathFile.lastModified() != ruleStamp) {
						log("parsing rewrite rules from " + pathFile.getPath());
						InputStream in = new FileInputStream(pathFile);
						rules = parseRules(in);
						in.close();
						ruleStamp = pathFile.lastModified();
					}
				}
			}
		}
		catch(IOException e) {
			throw new GossamerException(e);
		}
		return rules;
	}
	
	protected void log(String message) {
		LogManager logManager = (LogManager) applicationContext.getBean("logManager");
		logManager.writeLog("rewrite.log", message);
	}
	
	public RegexpEntry[] parseRules(InputStream in)
		throws IOException
	{
		List<RegexpEntry> rr = new ArrayList<RegexpEntry>();
		BufferedReader read = new BufferedReader(new InputStreamReader(in));
		String line;
		
		while((line = read.readLine()) != null)
		{
			if(line.startsWith("#")) {
// comment				
			} else {
				String [] ff = line.split("[\\s]+"); // split on whitespace
		
				if(ff.length > 2 && ff[0].length() > 0 && ff[0].charAt(0) != '#') {
					try {
						RegexpEntry en = new RegexpEntry();
						en.pattern = Pattern.compile(ff[0]);
						en.emitters = parseOutputExp(ff[1]);
						switch(ff[2].charAt(0)) {
						case 'C' : en.code = CONTINUE;	break;
						case 'R' : en.code = RETURN; 	break;
						case 'S' : en.code = START;		break;
						case 'F' : en.code = FILE;		break;
						default :
							log("illegal code used in rewite rules: " + ff[2].charAt(0));
							continue;
						}
						rr.add(en);
					} catch(PatternSyntaxException e) {
						log("regex error while parsing rules: " + line);
					}
				}
			}
		}
		return rr.toArray(new RegexpEntry[rr.size()]);
	}
	
	protected Emitter[] parseOutputExp(String s) {
		List<Emitter> l = new ArrayList<Emitter>();
		int n;
		int offset = 0;
		int len = s.length();
		while(offset < len) {
			n = s.indexOf('$',offset);
			if(n == -1) {
				l.add(new StringEmitter(s.substring(offset)));
				offset = len;
			} else if((n+1) < len) {
				if(offset < n) {
					l.add(new StringEmitter(s.substring(offset,n)));
					offset = n;
				}
				char c = s.charAt(n+1);
				if(c == '$') {
					l.add(new StringEmitter("$"));
					offset += 2;
				} else if(Character.isDigit(s.charAt(n+1))) {
					int j = 2;
					while(n+j < len && Character.isDigit(s.charAt(n+j))) {
						++j;
					}
					l.add(new PatternEmitter(Integer.parseInt(
							s.substring(n+1,n+j))));
					offset =  n+j;
				}
			} else {
				s=s.substring(0,--len);
				log("dangling $ at end of replacement expression, dropping it");
			}
			
		}
		return l.toArray(new Emitter[l.size()]);
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

}
