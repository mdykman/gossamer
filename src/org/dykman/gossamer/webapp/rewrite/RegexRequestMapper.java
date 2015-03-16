package org.dykman.gossamer.webapp.rewrite;

import java.io.File;
import java.util.regex.Matcher;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.dykman.gossamer.core.GossamerException;

public class RegexRequestMapper implements RequestMapper
{
	RegexRequestMapperFactory origin;
	
	public static final int RETURN = 0;
	public static final int START = 1;
	public static final int CONTINUE = 2;
	public static final int FILE = 3;

	private RegexpEntry[] rules;
	private File resourceBase;

	public RegexRequestMapper(RegexRequestMapperFactory or) {
		origin = or;
	}
	
	public void setResourceBase(File basePath) {
		resourceBase = basePath;
	}
	
	public void setRules(RegexpEntry [] entries) {
		rules = entries;
	}

	public String mapRequest(ServletRequest request)
	{
		HttpServletRequest req = (HttpServletRequest) request;
		String path = req.getServletPath();
		String pi = req.getPathInfo();
		if(pi != null) {
			path = path + pi;
		}
		return rewrite(path);
	}
	
	
	public String rewrite(String in)
	{
//System.out.print("REWRITE " + in + " -> ");		
		RegexpEntry[] rlz = rules;
		boolean redo = true;
		int retries = 1000;
		while(redo) {
			if(--retries == 0) {
				throw new GossamerException(
					"loop in rules.. maximum retries exceeded");
				
			}
			redo = false;
			
			for(RegexpEntry en : rlz) {
				Matcher m = en.pattern.matcher(in);
				origin.log("testing " + en.pattern.pattern() + " against " + in);	
				if(m != null && m.matches()) { // tested for match
					String translated = trans(m, en.emitters);

					if(en.code == RETURN) {
//System.out.println(in);		
						in = translated;
						break;
//						return translated; // return immediately
					}
					else if(en.code == CONTINUE)	{
						in = translated;
					} // let the loop complete
					else if(en.code == FILE)	{
//System.out.println("mapper: FILE " + translated);				
						if(translated.length() > 0) {
							File ff = new File(resourceBase,
								translated.substring(1));
							if(ff.isFile() && ff.canRead()) {
								in = translated;
								break;
//								return translated;
							}
						}
					} // let the loop complete
					else if(en.code == START) { // restart loop
						in =translated;
						redo = true; 
						break;
					}
				}
			}
			
		}
		origin.log("mapped to " + in);		
		return in;
	}
	
	private String trans(Matcher m, Emitter[] emitters) {
		StringBuffer buffer = new StringBuffer();
		for(Emitter em : emitters) {
			em.emit(m, buffer);
		}
		return buffer.toString();
	}
	
}
