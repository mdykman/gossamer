package org.dykman.gossamer.webapp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.dykman.gossamer.Constants;
import org.dykman.gossamer.device.OutputDevice;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ContentInterceptorFilter implements Filter {
	private ServletContext context;
	private ApplicationContext applicationContext;

	public void destroy()
	{
	}

	public void init(FilterConfig filterConfig) throws ServletException
	{
		context = filterConfig.getServletContext();
		applicationContext = WebApplicationContextUtils.getWebApplicationContext(context);
	}

	public void doFilter(
		ServletRequest servletRequest,
	    ServletResponse servletResponse, 
	    FilterChain filterChain)
	    throws IOException, ServletException
	{
		if (servletResponse instanceof HttpServletResponse) {
			HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
			ResponseWrapper responseWrapper = new ResponseWrapper(httpServletResponse);
			OutputDevice od = (OutputDevice)applicationContext.getBean(Constants.DEVICE);
			servletRequest.setAttribute("", od);
			
			filterChain.doFilter(servletRequest, responseWrapper);
			responseWrapper.getWriter().flush();

			od.setOutputStream(servletResponse.getOutputStream());
			od.execute((HttpServletRequest) servletRequest,
				responseWrapper, responseWrapper.byteBuffer.toByteArray());
		} else {
			filterChain.doFilter(servletRequest, servletResponse);
		}
	}

	static class ResponseWrapper extends HttpServletResponseWrapper
	{
		MyOutputStream	outputStream	= null;
		PrintWriter		writer		 = null;
		ByteArrayOutputStream	byteBuffer;

		public ResponseWrapper(HttpServletResponse response)
		{
			super(response);
			this.byteBuffer = new ByteArrayOutputStream();
			outputStream = new MyOutputStream();
			writer = new PrintWriter(outputStream);
		}

		@Override
		public PrintWriter getWriter()
		{
			return writer;
		}

		@Override
		public ServletOutputStream getOutputStream()
		{
			return outputStream;
		}

		public InputStream getResultAsInputStream()
		{
			return new ByteArrayInputStream(byteBuffer.toByteArray());
		}

		class MyOutputStream extends ServletOutputStream
		{
			@Override
			public void write(int c) throws IOException
			{
				byteBuffer.write(c);
			}

			@Override
			public void write(byte[] c) throws IOException
			{
				byteBuffer.write(c);
			}

			@Override
			public void write(byte[] c, int off, int len) throws IOException
			{
				byteBuffer.write(c, off, len);
			}

			@Override
			public void flush() throws IOException
			{
				byteBuffer.flush();
			}

			@Override
			public void close() throws IOException
			{
				this.flush();
			}
		}
	}
}
