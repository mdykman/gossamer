package org.dykman.gossamer.dexter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import org.apache.commons.httpclient.util.DateUtil;
import org.dykman.dexter.Dexter;
import org.dykman.dexter.DexterException;
import org.dykman.dexter.DocumentSerializer;
import org.dykman.dexter.base.DexterEntityResolver;
import org.dykman.gossamer.core.SourceLocator;
import org.dykman.gossamer.device.ClientDeviceProfile;
import org.dykman.gossamer.webapp.GossamerServlet;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DexterServlet2 extends GossamerServlet {
	private static final long serialVersionUID = -7917552370845724246L;
	protected static final String DEXTER_LOG = "dexter.log";
	protected static final String ONE_YEAR_IN_SECONDS = (new Integer(
			60 * 60 * 24 * 365)).toString();

	public static final String GOSSAMER_ERROR_FILE = "default/xhtml-1.0/system/notfound.html.xsl";
	File resourceRoot;

	String contentType = "application/xslt+xml";
	boolean passthrough = false;

	TransformerFactory transformerFactory;
	DocumentBuilderFactory factory;
	Pattern taggedOutput;
	boolean validate = false;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			String path = config.getInitParameter("contentType");
			if (path != null)
				contentType = path;

			passthrough = !"true".equalsIgnoreCase((String)gossamerProperties
					.get("gossamer.dexter.enable"));
			this.addCredit(Dexter.DEXTER_VERSION);

//			config.getServletContext().getRealPath(arg0)
			path = config.getInitParameter("xsl-files");
			if(path == null) path ="xsl";
			resourceRoot = new File(GossamerInstallationBase,path);
			if (!resourceRoot.isDirectory())
				throw new ServletException(path + " is not a directory");
			transformerFactory = TransformerFactory.newInstance();
			factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setExpandEntityReferences(false);
			factory.setCoalescing(true);
			factory.setIgnoringComments(false);

			taggedOutput = Pattern.compile(".*?[$]([a-zA-Z0-9]+)[.].*");
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	protected File processXslRequest(String uri) throws IOException {
		File result = null;
		File ff = new File(resourceRoot, uri);
		Matcher matcher = taggedOutput.matcher(uri);
		if ((!matcher.matches()) || !ff.exists()) {
			SourceLocator sl = (SourceLocator) applicationContext
					.getBean("sourceLocator");
			File sourceFile = sl.locateSource(sl.guessSource(uri));

			if (sourceFile != null && sourceFile.exists()) {
				String gh = getSourceHash(sourceFile);
				if (matcher.matches()) {
					String hash = matcher.group(1);
					ff = new File(ff.getParentFile(), ff.getName().replace(hash, gh));
				}
				if (!ff.exists()) {
					try {
						synchronized(DexterServlet2.class) {
							try {
								if(!ff.exists()) runDexter(sourceFile, ff, gh);
							} catch(Exception e) {
								System.out.println("error!! " + e.getLocalizedMessage());
								e.printStackTrace();
								throw e;
							}
						}
					} catch (Exception e) {
						if (!ff.exists()) {
							boolean created = ff.createNewFile();
							if (created) {
								copyErrorFile(new File(resourceRoot,
										GOSSAMER_ERROR_FILE), ff,
										"an error occured compiling "
												+ sourceFile.getPath() + " : "
												+ e.getMessage());
								result = ff;
							} else {
								System.out
										.println("hmm..   i'm pretty screwed up by here, i think");
							}
						}
					}
				}
			}
		}
		if (ff.exists())
			result = ff;

		return result;
	}

	protected String getSourceHash(File f) {
		Long ts = f.lastModified();
		return Long.toHexString(ts);
	}

	private void copyErrorFile(File from, File to, String message)
			throws java.io.IOException {
		BufferedReader reader = new BufferedReader(new FileReader(from));
		String line = null;
		Writer writer = new FileWriter(to);
		while ((line = reader.readLine()) != null) {
			line = line.replace("__GOSSAMER_ERROR_MESSAGE__", message);
			writer.write(line);
			writer.write("\n");
		}
		writer.close();
		reader.close();
	}

	public String generateXmlStub(String xslname) {
		StringBuilder builder = new StringBuilder();
		builder.append("<?xml version=\"1.0\"?>\n")
			.append("<?xsl-stylesheet type=\"text/xsl\" href=\"/xsl/").append(xslname).append("\" ?>\n\n")
			.append("<xml/>\n");
		return builder.toString();
	}
	public boolean previewXsl(
			HttpServletResponse response, 
			ClientDeviceProfile device,
			String uri) 
		throws IOException {
		boolean result = false;
			
		Writer writer = response.getWriter();
		SourceLocator sl = (SourceLocator) applicationContext
				.getBean("sourceLocator");
		File source = sl.locateSource(uri + ".xsl");
		if(source.exists()) {
			String ct = "text/xml";
			String c = device.getAttribute("xml_mime_type");
			if(c!= null) ct = c;
			response.setContentType(ct);
			String name = sl.formOutputName(source);
			writer.write(generateXmlStub(name));
			writer.flush();
			result = true;
		}
		return result;
	}
	@Override
	public boolean serve(HttpServletRequest request,
			HttpServletResponse response, ClientDeviceProfile device)
			throws javax.servlet.ServletException, java.io.IOException {

		String uri = request.getPathInfo().substring(1);

		File initialTarget = new File(resourceRoot, uri);
		File result = initialTarget;

		String ct = contentType;
		// File target = initialTarget;
		// is it xsl
		if(uri.endsWith(".xsl")) {
			if(uri.startsWith("$")) {
				uri = uri.substring(1);
				return previewXsl(response,device,uri);
			}	else {
				String cc = device.getAttribute("xsl1.0_mime_type");
				if (cc != null) {
					ct = cc;
				}
				result = processXslRequest(uri);
				if (result == null) {
					if (!initialTarget.exists()) {
						boolean created = initialTarget.createNewFile();
						if (created) {
							copyErrorFile(new File(resourceRoot,
									GOSSAMER_ERROR_FILE), initialTarget,
									"no file found: " + uri);
							result = initialTarget;
						} else {
							System.out
									.println("hmm..   i'm pretty screwed up by here, i think");
						}
					}
				}
				return serve(result, request, response, ct);
			}
		} else {
			return serve(initialTarget, request, response);
		}
	}

	protected void configureDexter(Dexter dexter, DocumentBuilder builder,
			File in, String hash) throws IOException {
		dexter.setIdHash(hash);

		File dexConfig = new File(in.getParentFile(), ".dexter");
		if (dexConfig.exists()) {
			Properties props = new Properties();
			InputStream is = new FileInputStream(dexConfig);
			props.load(is);
			is.close();
			String p = props.getProperty("method");
			if (p != null) {
				writeLog(DEXTER_LOG, "setting method to " + p);
				dexter.setMethod(p);
			}
			p = props.getProperty("media-type");
			if (p != null) {
				writeLog(DEXTER_LOG, "setting media-type to " + p);
				dexter.setMediaType(p);
			}
			p = props.getProperty("import");
			if (p != null) {
				String[] ll = p.split("[,]");
				List<String> lst = new ArrayList<String>();
				File parent = in.getParentFile();
				for (String lb : ll) {
					writeLog(DEXTER_LOG, "adding library " + lb);
					lst.add(new File(parent, lb.trim()).getAbsolutePath());
				}
				dexter.loadLibraryTemplate(builder, lst);
			}
		}
	}

	static class MyErrorHandler implements ErrorHandler {
		StringBuilder sb = new StringBuilder();

		public String getMessage() {
			return sb.toString();
		}

		protected void report(String label, SAXParseException exception) {
			sb.append("<div class=\"").append(label).append("\">").append(label).append("! ")
				.append(exception.getSystemId()).append(" line ")
					.append(exception.getLineNumber()).append(": ")
					.append(exception.getMessage()).append("</div>\n");

		}

		@Override
		public void warning(SAXParseException exception) throws SAXException {
			report("Warning", exception);
		}

		@Override
		public void fatalError(SAXParseException exception) throws SAXException {
			report("Fatal", exception);
		}

		@Override
		public void error(SAXParseException exception) throws SAXException {
			report("Error", exception);
		}
	};

	protected boolean runDexter(File in, File expected, String hash) 
//		throws Exception 
		{
			writeLog(DEXTER_LOG, "compiling source " + in.getAbsolutePath());
	
			DocumentBuilder builder = null;
			MyErrorHandler errorHandler = new MyErrorHandler();
			boolean result = false;
			try {
				builder = factory.newDocumentBuilder();
				builder.setEntityResolver(new DexterEntityResolver("UTF-8"));
				builder.setErrorHandler(errorHandler);
		
				Dexter dexter = new Dexter("UTF-8", null, builder);
				configureDexter(dexter, builder, in, hash);
				Map<String, Document> docs = dexter.generateXSLT(in);
	
			File directory = expected.getParentFile();
			directory.mkdirs();
			for (Map.Entry<String, Document> entry : docs.entrySet()) {
				String k = entry.getKey();
				File kf = new File(k);
				File out = new File(directory, kf.getName());
				writeLog(DEXTER_LOG, "writing output file " + out.getAbsolutePath());
				putToDisk(out, entry.getValue(),dexter);
				result = true;
			}
			writeLog(DEXTER_LOG, "successfully compiled source " + in.getAbsolutePath());
			} catch(Exception saxe) {
				//TODO:: mid-report improvement
				String s = errorHandler.getMessage();
			}
			return result && expected.exists();
	}

	private static void putToDisk(File f, Document doc, Dexter dexter)
			throws IOException {
		dexter.collectEntities(doc);
		Writer writer = new FileWriter(f);
		write(doc, writer, "UTF-8", dexter.collectEntities(doc));
		writer.close();
	}

	protected static void write(Document document, Writer writer,
			String encoding, Map<String, String> entities) throws IOException {
		try {
			DocumentSerializer serializer = new DocumentSerializer(encoding,true);
			serializer.setEntities(entities);
			serializer.serialize(document, writer);
		} catch (Exception e) {
			throw new DexterException("error while rendering document: "
					+ e.getMessage(), e);
		}
	}

	@SuppressWarnings("unused")
	private boolean validateXsl(File file)
			throws TransformerConfigurationException {
		return true;
	}

	static class MyErrorListener implements ErrorListener {
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

	protected void setCacheHeaders(HttpServletResponse response) {
		response.setHeader("Cache-Control", "max-age=" + ONE_YEAR_IN_SECONDS);

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 1);
		response.setHeader("Expires",
				DateUtil.formatDate(cal.getTime(), DateUtil.PATTERN_RFC1123));

	}

}
