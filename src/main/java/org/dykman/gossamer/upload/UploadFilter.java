package org.dykman.gossamer.upload;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.dykman.gossamer.core.GossamerException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class UploadFilter implements Filter
{
	private ApplicationContext applicationContext;
	public UploadFilter()
	{ }

	public void init(FilterConfig config) throws ServletException
	{
		ServletContext context = config.getServletContext();
		applicationContext = WebApplicationContextUtils
			.getWebApplicationContext(context);
	}

	
	public String nameFile(String name) {
		return name;
	}
	
	public void destroy() { }

	public void doFilter(ServletRequest request, ServletResponse response,
	        FilterChain filterChain) throws IOException, ServletException {
		if(ServletFileUpload.isMultipartContent((HttpServletRequest)request)) {
			DiskFileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			UploadManager uploadManager = null;
			
			// parse the parameters
			try {
				@SuppressWarnings("unchecked")
				List<FileItem> items = (List<FileItem>)
					upload.parseRequest((HttpServletRequest)request);
				Map<String,String> params = new HashMap<String, String>();
				for(FileItem item : items) {
					String k = item.getFieldName();
					if(!item.isFormField())
					{
						if(uploadManager == null)
							uploadManager = (UploadManager) 
								applicationContext.getBean("uploadManager");

						params.put(k, uploadManager.persist((HttpServletRequest)request,item));
					}
					else {
						params.put(k, item.getString());
					}
				}
				
				ServletRequest newReq  = new FileFilteredRequest(
					(HttpServletRequest)request,params); 
				filterChain.doFilter(newReq, response);
			} catch(Exception e) {
				throw new GossamerException(e);
			}
		} else {
			filterChain.doFilter(request, response);
		}
	}

	static class FileFilteredRequest extends HttpServletRequestWrapper {
		Map<String,String>params = new HashMap<String, String>();
		
		public FileFilteredRequest(HttpServletRequest req, Map<String,String>params) {
			super(req);
			this.params = params;
		}
		
		@Override
		public String getParameter(String key) {
			return params.get(key);
		}
		
		@Override
		public Map<String,?> getParameterMap() {
			return params;
		}
		
		@Override
		public Enumeration<String> getParameterNames() {
			return new En(params.keySet());
		}
		@Override
		public String[] getParameterValues(String key) {
			return new String[] {params.get(key)};
		}
		
		class En implements Enumeration<String> {
			Iterator<String> it;
			En(Collection<String> c)
			{
				it = c.iterator();
			}
			public boolean hasMoreElements()
			{
				return it.hasNext();
			}
			public String nextElement()
			{
				return it.next();
			}
		}
		
	}
}
