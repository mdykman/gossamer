package org.dykman.gossamer.webapp.rewrite;

import java.io.IOException;
import java.net.URL;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dykman.gossamer.core.StringContainer;
import org.dykman.gossamer.webapp.LoggingFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class MappingFilter extends LoggingFilter {
	RequestMapper requestMapper;

	public void init(FilterConfig config) throws ServletException {
		ServletContext context = config.getServletContext();
		ApplicationContext applicationContext = WebApplicationContextUtils
				.getWebApplicationContext(context);
		requestMapper = (RequestMapper) applicationContext
				.getBean("requestMapper");

	}

	public void destroy() {
	}

	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain filterChain) throws IOException, ServletException {
		// look for relevant mappings
		String forward = requestMapper.mapRequest(req);
		HttpServletRequest request = (HttpServletRequest) req;
		if (forward != null) {
			log("mappingfilter forwarding " + request.getServletPath() + " to " + forward);
			req.getRequestDispatcher(forward).forward(req, res);
		} else {
			log("mappingfilter passing through");
			filterChain.doFilter(req, res);
		}
	}
}
