package org.dykman.gossamer.webapp.rewrite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;


public class SimpleRequestMapper implements RequestMapper
{
	List<String> passthoughDirectories = new ArrayList<String>();
	Map<String, String> mappings = new HashMap<String, String>();
	String deflt;
	
	public void setPassthough(List<String> passthoughDirectories)
    {
    	this.passthoughDirectories = passthoughDirectories;
    }

	public List<String> getPassthough()
    {
    	return this.passthoughDirectories;
    }
	
	public void setDefault(String s)
	{
		deflt = s;
	}
	
	public void setMappings(Map<String,String> mappings)
    {
		this.mappings = mappings;
    }

	public String mapRequest(ServletRequest req)
	{
		HttpServletRequest request = (HttpServletRequest) req;
		String path = request.getServletPath();
		String [] pp = path.split("[/]");

		String token;
		if( pp.length > 0 && (pp[1].indexOf('.') != -1 
			|| passthoughDirectories.contains(pp[1])))
		{
			return null;
		}
		else if(pp.length < 2)
		{
			token = deflt;
		}
		else
		{
			token = pp[1];
		}
		
		String m = mappings.get(token);
		if(m == null)
		{
			m = mappings.get("*");
		}
		return m == null ? null : m + path;
	}

	public Map<String, String> getMappings()
    {
    	return mappings;
    }
}
