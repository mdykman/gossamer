package org.dykman.gossamer.dexter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.httpclient.util.DateUtil;
import org.dykman.dexter.Dexter;
import org.dykman.dexter.DexterException;
import org.dykman.dexter.HackWriter;
import org.dykman.dexter.base.DexterEntityResolver;
import org.dykman.gossamer.device.ClientDeviceProfile;
import org.dykman.gossamer.webapp.GossamerServlet;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DexterServlet extends GossamerServlet
{
	private static final long serialVersionUID = -7917552370845724246L;
	protected static final String	DEXTER_LOG = "dexter.log";	
	protected static final String	ONE_YEAR_IN_SECONDS	
		= (new Integer(60 * 60 * 24 * 365)).toString();

	File resourceRoot;
	
	String contentType = "application/xslt+xml";
	boolean passthrough = false;
	
	TransformerFactory transformerFactory;
	DocumentBuilderFactory factory;
	
	boolean validate = false;

	@Override
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
		try {
			String path = config.getInitParameter("contentType");
			if(path != null) contentType = path;
	
			passthrough =  ! "true".equalsIgnoreCase((String)gossamerProperties.get("gossamer.dexter.enable"));
			this.addCredit(Dexter.DEXTER_VERSION);
			
			path = (String)gossamerProperties.get("gossamer.xsl.files");
			resourceRoot = new File(path);
			if (!resourceRoot.isDirectory()) throw new ServletException(path + " is not a directory");
			transformerFactory = TransformerFactory.newInstance();
			factory  = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setExpandEntityReferences(false);
			factory.setCoalescing(true);
			factory.setIgnoringComments(false);
	
			path = (String)gossamerProperties.get("gossamer.dexter.validate");
			if(path != null) {
				validate = "true".equals(path);
			}
		} catch(Exception e) {
			throw new ServletException(e);
		}
	}
	public String guessSource(String s) {
//		System.out.println("Guessing source from " + s);
		int n = s.lastIndexOf('-');
		if(n != -1) s = s.substring(0,n);
		System.out.println("Guessed " + s);
		return s;
	}

	protected File processXslRequest(String uri) {
		File result = null;
		String sourceStr = guessSource(uri);
		if(sourceStr == null) {
			File ff = new File(resourceRoot,uri);
			if(ff.exists()) {
				result = ff;
			} else {
				// TODO:: explain the error with prepared stylesheet
			}
		} else {
			File source = new File(sourceStr);
			int n = uri.lastIndexOf('$');
			if(n != -1) {
				
			} else {
				if(source.exists()) {
					String hash = getSourceHash(source);
					result = seekOutput(source,hash);
				} else {
					
				}
			}
		}
		
		return result;
	}
	protected File seekOutput(File source,String hash) {
		File result = null;
		String path = source.getPath();
		int n = path.lastIndexOf('.');
		if(n != -1) {
			StringBuilder sb = new StringBuilder();
			sb.append(path.substring(0,n)).append('$')
				.append(hash).append(path.substring(n))
				.append(".xsl");
			File rr = new File(resourceRoot,sb.toString());
			if(rr.exists()) {
				result = rr;
			}
		}
		return result;
	}
	
	protected String getSourceHash(File f) {
		Long ts = f.lastModified();
		return Long.toHexString(ts);
	}
	
	protected File findSource(File target) {
		String uri = target.getPath();
		int n = uri.lastIndexOf('$');
		boolean modified = n != -1;
		File source = null;
		if(modified) {
			String base = uri.substring(0,n);
			String tail = uri.substring(n+1);
			
			n = tail.indexOf('.');
			String suffix = tail.substring(n,tail.length() -4);
			String fn = base + suffix;
//find the source file				
			source = new File(resourceRoot,fn);
writeLog(DEXTER_LOG, "scanning for source " + target.getPath());

			if(!source.exists()) {
				
			}
		} else {
			n = uri.lastIndexOf('.');
		}
		return source;
		
	}
	
	@Override
	public boolean serve(HttpServletRequest request, HttpServletResponse response, ClientDeviceProfile device)
	        throws javax.servlet.ServletException, java.io.IOException
	{

		String uri = request.getPathInfo().substring(1);
		
		File initialTarget = new File(resourceRoot,uri);
		File target = initialTarget;
// is it xsl
		if (uri.endsWith(".xsl")) {
			String ct = contentType;
			String cc = device.getAttribute("xsl1.0_mime_type");
			if(cc != null) {
				ct = cc;
			}
// serve it if you have it
			if(target.exists()) {
				return serve(target,request,response,ct);
			} else {
				int n = uri.lastIndexOf('$');
				boolean modified = n != -1;
				File fft;
				if(modified) {
					String base = uri.substring(0,n);
					String tail = uri.substring(n+1);
					
					n = tail.indexOf('.');
					String suffix = tail.substring(n,tail.length() -4);
					String fn = base + suffix;
// find the source file				
					target = new File(resourceRoot,fn);
writeLog("dexter.log", "scanning for source " + target.getPath());

					if(!target.exists()) {
						// maybe it's a sub
						target = new File(resourceRoot,guessSource(fn));
writeLog("dexter.log", "... looking for possible parent source " + target.getPath());
						if(!target.exists()) {
							response.setStatus(404);
							return false;
						}
					}
			
					Long ts = target.lastModified();
					String fhash = Long.toHexString(ts);
					String ft = base + '$' + fhash + tail.substring(n);
					fft = new File(resourceRoot,ft);
					if(! fft.exists()) {
						runDexter(target,initialTarget,fhash);
					} 
				} else {
					target = new File(resourceRoot,uri.substring(0,uri.length()-4));					
					
					runDexter(target,initialTarget,null);
					
				}

				serve(initialTarget,request,response,ct);
			}
			return true;
		} else {
			return serve(target,request,response);
		}
	}

	protected void configureDexter(Dexter dexter,DocumentBuilder builder, File in,String hash)
		throws IOException {
		if(hash != null) dexter.setIdHash(hash);
		
		File dexConfig = new File(in.getParentFile(),".dexter");
		if(dexConfig.exists()) {
			Properties props = new Properties();
			InputStream is = new FileInputStream(dexConfig);
			props.load(is);
			is.close();
			String p = props.getProperty("method");
			if(p != null) {
				writeLog(DEXTER_LOG,"setting method to " + p);
				dexter.setMethod(p);
			}
			p = props.getProperty("media-type");
			if(p != null) {
				writeLog(DEXTER_LOG,"setting media-type to " + p);
				dexter.setMediaType(p);
			}
			p = props.getProperty("import");
			if(p != null)  {
				String [] ll = p.split("[,]");
				List<String> lst = new ArrayList<String>();
				File parent = in.getParentFile();
				for(String lb : ll) {
					writeLog(DEXTER_LOG,"adding library " + lb);
					lst.add(new File(parent,lb.trim()).getAbsolutePath()); 
				}
				dexter.loadLibraryTemplate(builder,lst);
			}
		}
	}

	class MyErrorHandler implements ErrorHandler  {
		StringBuilder sb = new StringBuilder();
			public String getMessage() {
			return sb.toString();
		}

		protected void report(String label,SAXParseException exception) {
			sb.append("<div>").append(label).append("line ").append(exception.getLineNumber())
				.append(": ").append(exception.getMessage()).append("</div>\n");
				
		}

		@Override
		public void warning(SAXParseException exception) throws SAXException {
			report("Warning: ", exception);
		}
		
		@Override
		public void fatalError(SAXParseException exception) throws SAXException {
			report("Fatal: ",exception);
		}
		
		@Override
		public void error(SAXParseException exception) throws SAXException {
			report("Error: ",exception);
		}
	};

	
	protected File createErrorStyle(File f,String message) throws ServletException {
		try {
			OutputStream out = new FileOutputStream(f);
			byte [] buff = new byte[8192];
			int n;
	
			InputStream in = getClass().getResourceAsStream("error-start");
			while((n = in.read(buff))!=-1) {
				out.write(buff,0,n);
			}
			in.close();
	
			out.write(message.getBytes());
			
			in = getClass().getResourceAsStream("error-end");
			while((n = in.read(buff))!= -1) {
				out.write(buff,0,n);
			}
			in.close();
			
			out.close();
			return f;
		} catch(IOException e) {
			throw new ServletException(e);
		}
	}
	
	protected File runDexter(File in,File expected,String hash)
	{
		File result = null;
		writeLog(DEXTER_LOG,"compiling source " + in.getAbsolutePath());

		try {
//			Document source = null;
			DocumentBuilder builder = null;
			MyErrorHandler errorHandler = new MyErrorHandler();
			try {
				builder = factory.newDocumentBuilder();
				builder.setEntityResolver(new DexterEntityResolver("UTF-8"));
				builder.setErrorHandler(errorHandler);
//				FileInputStream fin = new FileInputStream(in);
//				source = builder.parse(fin);
//				fin.close();
			} catch(Exception e) {
				// THIS WAS OVERWRITING THE SOURCE FILE!!
				//return createErrorStyle(in,errorHandler.getMessage());

				writeLog(DEXTER_LOG,"exception encountered parsing " + in.getAbsolutePath() + ": " + errorHandler.getMessage());

				Dexter dexter = new Dexter("UTF-8", null, builder);
				configureDexter(dexter,builder,in,hash);

				String name = in.getName();
				StringBuilder sb = new StringBuilder();

				int n = name.lastIndexOf('.');
				sb.append(name.substring(0,n));

				if(hash != null)
					sb.append('$').append(hash);

				sb.append(name.substring(n));

				sb.append(".xsl");

				name = sb.toString();
//				@SuppressWarnings("unused")
//				Map<String,Document> docs = null;

				dexter.setMediaType("text/html");
				dexter.setMethod("xml");
/*
				try {
					docs = dexter.generateXSLT(in);
				} catch(Exception e2) {
					writeLog(DEXTER_LOG,"secondary exception encountered writing error file: " + e2.getMessage());
				}
*/
				File directory = in.getParentFile();

				writeLog(DEXTER_LOG,"creating file " + name + " in " + directory);

				File out = new File(directory,name);

				if(!name.endsWith(".dispose.xsl")) {
					writeLog(DEXTER_LOG,"creating file " + out.getAbsolutePath());
					return createErrorStyle(out,errorHandler.getMessage());
				}

				return out;
			}
			
			Dexter dexter = new Dexter("UTF-8", null, builder);
			configureDexter(dexter,builder,in,hash);
			
			String name = in.getName();
			StringBuilder sb = new StringBuilder();
			int n = name.lastIndexOf('.');
			sb.append(name.substring(0,n));
			if(hash != null) sb.append('$').append(hash);
			sb.append(name.substring(n));
	
			name = sb.toString();
			Map<String,Document> docs = null;
			try {
				docs = dexter.generateXSLT(in);
			} catch(Exception e) {
//				return createErrorStyle(in,e.getMessage());
				writeLog(DEXTER_LOG,"exception encountered parsing " + in.getAbsolutePath() + ": " + errorHandler.getMessage());

				dexter = new Dexter("UTF-8", null, builder);
				configureDexter(dexter,builder,in,hash);

				//String name = in.getName();
				StringBuilder sb2 = new StringBuilder();

				sb2.append(name);

				/*
				n = name.lastIndexOf('.');
				sb2.append(name.substring(0,n));

				if(hash != null)
					sb2.append('$').append(hash);

				sb2.append(name.substring(n));
				*/

				sb2.append(".xsl");

				name = sb2.toString();
//				Map<String,Document> docs = null;

				dexter.setMediaType("text/html");
				dexter.setMethod("xml");
				try {
					docs = dexter.generateXSLT(new File(name));
				} catch(Exception e2) {
					writeLog(DEXTER_LOG,"secondary exception encountered writing error file: " + e2.getMessage());
				}

				File directory = in.getParentFile();

				writeLog(DEXTER_LOG,"creating file " + name + " in " + directory);

				File out = new File(directory,name);

				if(!name.endsWith(".dispose.xsl")) {
					writeLog(DEXTER_LOG,"creating file " + out.getAbsolutePath());
					return createErrorStyle(out,errorHandler.getMessage());
				}

				return out;
			}
	
			File directory = in.getParentFile();
			for(Map.Entry<String, Document> entry : docs.entrySet()) {
				String k = entry.getKey();
				File out = new File(directory,k);
				if(k.equals(name + ".xsl")) {
					result = out;
				}
				if(!k.endsWith(".dispose.xsl")) {
					writeLog(DEXTER_LOG,"creating file " + out.getAbsolutePath());
					putToDisk(out, entry.getValue());
				}
			}
		
			if(validate) {
				boolean r = validateXsl(result);
				writeLog(DEXTER_LOG,"validating result " + in.getAbsolutePath() 
						+ (r ? " success" : " fail" ));
			}
			writeLog(DEXTER_LOG,"successfully compiled source " + in.getAbsolutePath());
		} catch(Exception e) {
			writeLog(DEXTER_LOG,"Exception compiling " + in.getAbsolutePath() + " " + e.getMessage());
			result = new File(resourceRoot,"system/error.html.xsl");
		}
		return result;
	}
	File findDexterSourceFile(String source) 
		throws IOException {
		
		File result = null;
		String [] bits = source.split("[/]");
		int n = 0;
System.out.println("findDexterSourceFile");
		for(String s : bits) {
			System.out.println("" + n + " " + s);
			++n;
		}
System.out.println();

		File f = new File(resourceRoot,source);
		
		
		return result;
	}

	File generateErrorStyle(Exception e) {
		return null;
	}
	private boolean validateXsl(File file) 
		throws TransformerConfigurationException {
		boolean result;
		ErrorListener el = new MyErrorListener();
		StreamSource source = new  StreamSource(file);
		source.setSystemId(file);
		transformerFactory.setErrorListener(el);
		transformerFactory.newTransformer(source);
		result = true;
		return result;
	}
	
	class MyErrorListener implements ErrorListener {
		StringBuilder sb = new StringBuilder();
		
		protected void report(TransformerException exception) {
			sb.append(exception.getMessageAndLocation());
		}
		@Override
		public void error(TransformerException exception)
				throws TransformerException {
			report(exception);
		}
		@Override
		public void fatalError(TransformerException exception)
				throws TransformerException {
			report(exception);
		}
		@Override
		public void warning(TransformerException exception)
				throws TransformerException {
			report(exception);
		}
		public String getMessage() {
			return sb.toString();
		}
	}
	@SuppressWarnings("unchecked")
	private void putToDisk(File out, Document doc) throws Exception
	{
		HackWriter writer = new HackWriter(new FileWriter(out));
		writer.setEntities((Map<String,String>)doc.getUserData("entity-map"));
		
		write(doc, writer, "UTF-8");
		writer.close();
	}
	
	protected void write(Document document, Writer writer, String encoding)
	{
		try
		{
			Transformer tranformer = transformerFactory.newTransformer();
			tranformer.setOutputProperty("indent", "no");
			tranformer.setOutputProperty("method", "xml");
			tranformer.setOutputProperty("media-type","text/xsl");
			tranformer.setOutputProperty("encoding", encoding);

			Result result = new javax.xml.transform.stream.StreamResult(writer);
			Source source = new javax.xml.transform.dom.DOMSource(document);

			tranformer.transform(source, result);
			writer.write("\n");
		}
		catch (Exception e)
		{
			throw new DexterException("error while rendering document: " 
					+ e.getMessage(),e);
		}
	}	
	
	protected void setCacheHeaders(HttpServletResponse response) {
		response.setHeader("Cache-Control", "max-age=" + ONE_YEAR_IN_SECONDS);

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 1);
		response.setHeader("Expires", DateUtil.formatDate(cal.getTime(),
		        DateUtil.PATTERN_RFC1123));

	}

}
