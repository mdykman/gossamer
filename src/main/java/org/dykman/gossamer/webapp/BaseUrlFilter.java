package org.dykman.gossamer.webapp;

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

import org.dykman.gossamer.core.StringContainer;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class BaseUrlFilter implements Filter {

	ApplicationContext applicationContext;
	boolean active = false;
	@Override
	public void init(FilterConfig config) throws ServletException {
		ServletContext context = config.getServletContext();
		applicationContext = WebApplicationContextUtils
				.getWebApplicationContext(context);
		active = applicationContext.containsBean("baseUrl");
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;

		if (active) {
			StringContainer host = (StringContainer) applicationContext
					.getBean("baseUrl");
			URL me = new URL(request.getRequestURL().toString());
			String ctx = request.getContextPath();
			boolean needsTail = true;
			if (ctx == null || ctx.length() == 0) {
				ctx = "/";
				needsTail = false;
			}
			URL base = new URL(me.getProtocol(), me.getHost(), me.getPort(),
					ctx);
			host.setValue(base.toExternalForm() + (needsTail ? "/" : ""));
		}
		filterChain.doFilter(req, res);

	}


}
