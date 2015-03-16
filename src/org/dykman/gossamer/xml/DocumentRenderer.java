package org.dykman.gossamer.xml;

import java.io.File;
import java.io.Writer;
import java.net.URL;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.dykman.gossamer.core.GossamerException;
import org.dykman.gossamer.core.Renderer;
import org.dykman.gossamer.device.ClientDeviceProfile;
import org.dykman.gossamer.util.NetUtils;
import org.dykman.gossamer.webapp.SiteProperties;
import org.dykman.gossamer.xsl.XslResolver;
import org.dykman.gossamer.xsl.XslTransformerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ProcessingInstruction;

public class DocumentRenderer implements Renderer
{
	// configed by spring
	private XslResolver xslResolver; 
//	private boolean trace = false;
	private SiteProperties siteProperties;
	private ClientDeviceProfile device = null;
	private XslTransformerFactory transformerFactory;
	private Boolean hasxsl = null;
	private String xmlMimeType = "text/xml";
	
	private boolean force = false;
	private String view;
	private String	   encoding = "UTF-8";
	private DateFormat dateFormat = null;

	public DocumentRenderer() {
		dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.LONG);
	}
	public void setSiteProperties(SiteProperties sp) {
		siteProperties = sp;
	}
	public void setXslResolver(XslResolver w) {
		xslResolver = w;
	}
	
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void render(Object o, Writer w) throws Exception {
		render(o,w,false);
	}
		
	private boolean hasXsl() {
		if(hasxsl == null) {
			hasxsl = device.getBoolean("xsl-1.0");
		}
		return hasxsl;
	}
	
	
	public void setXmlMimeType(String s) {
		xmlMimeType = s;
	}
	public String getContentType() {
		if(hasXsl() && ! force) {
			return xmlMimeType;
		} else {
			String fmt = xslResolver.getFormat();
			return device.getAttribute(fmt + "_mime_type");
		}
	}
	
	private String deToken(String s) {
		int n = s.lastIndexOf('.');
		if(n != -1) {
			String t = s.substring(0,n);
			int o = t.lastIndexOf('$');
			if(o != -1) {
				return new StringBuilder()
					.append(t.substring(0,o))
					.append(s.substring(n)).toString(); 
			}
		}
		return s;
	}
	private String mightBeSubdoc(String s) {
		String ext = "";
		if(s.endsWith(".xsl")) {
			s = s.substring(0,s.length() -4);
			ext = ".xsl";
		}
		int n = s.lastIndexOf('.');
		int o = s.lastIndexOf('-');
		if(n < o) {
			return s.substring(0,s.indexOf('-',n)) + ext;	
		}
		return null;
	}
	private void saveTransformData(File logData,TransformerFactory factory,Document data, String url) {
//		System.out.println("saveTransformData called");
		if(url.indexOf('$') == -1) {
			return;
		}

//		System.out.println("\tsaveTransformData processing");

		try {
			String subdoc = mightBeSubdoc(url);
//			System.out.println("  STD saveTR starts with " + url);

			if(subdoc != null) {
				url = subdoc;
				//System.out.println("\tsaveTransformData treating as subdoc");
			}

//			System.out.println("  STD saveTR continues with " + url);
	
			URL nurl = new URL(url);
			String ff = nurl.getFile();
			String src = deToken(ff.substring(0,ff.length()-4));
			
			// make the test directory
			File dataStore = new File(logData,src);
			dataStore.mkdirs();
			File dataFile = new File(dataStore,"data.xml");

			for(int i = 0;  dataFile.exists(); ++i) {
				dataFile = new File(dataStore,"data-" + i + ".xml");
			}
			
			// save the data file
			Source domsource = new DOMSource(data);
			Result domresult = new StreamResult(dataFile);
			Transformer transformer = factory.newTransformer();
			transformer.transform(domsource, domresult);
			//System.out.println("\tdata saved");
	
//			File srcFile
			int n = src.lastIndexOf('/');
//			src = src.substring(n+1);

			// TODO: this could be more elegant..  don't like the hards-coding of localhost
			dataFile = new File(dataStore,src.substring(n+1));

			URL srcUrl = nurl;
//			URL srcUrl = new URL(nurl.getProtocol(), "localhost", nurl.getPort(), src);
//			URL srcUrl = new URL(nurl.getProtocol(), nurl.getHost(), nurl.getPort(), src);
//			System.out.println("\tsaving source " + srcUrl.toExternalForm());
			NetUtils.dumpURL(srcUrl, dataFile);

			n = url.lastIndexOf('/');
			dataFile = new File(dataStore,url.substring(n+1));
			/*			
//			srcUrl = new URL(nurl.getProtocol(), nurl.getHost(), nurl.getPort(), nurl.getFile());
			srcUrl = new URL(nurl.getProtocol(), "localhost", nurl.getPort(), nurl.getFile());
*/			
//			System.out.println("\tsaving something else " + srcUrl.toExternalForm());
			NetUtils.dumpURL(srcUrl, dataFile);
//			System.out.println("\tvalidating " + srcUrl.toExternalForm());

			transformer = transformerFactory.getTransformer(url);
		} catch(Exception e) {
			System.out.print("error capturing trace data: " + e.getMessage());
			e.printStackTrace();
		}

	}
	
	public void render(Object o, Writer w, boolean in) throws Exception {
		StringBuilder sbTrace = new StringBuilder();

		if (view == null)
			sbTrace.append("<null view>");
		else
			sbTrace.append(view);

		try {
			DocumentConverter converter = new DocumentConverter();
			converter.setDateFormat(dateFormat);
			
			Document document = converter.render(o, in);
			if(siteProperties != null) siteProperties.appendSiteProperties(document);
			TransformerFactory tf = TransformerFactory.newInstance();

			
			Transformer transformer = null;
			if(view != null) {
				String xslPath = view;
//				String xslPath = xslResolver.resolveXslUrl(device, view);
				String dataf = siteProperties.get("gossamer.xsl.trace");

				sbTrace.append(" (" + xslPath +")");

				if(dataf != null) {
					saveTransformData(new File(dataf), tf, document, xslPath);
				}
				
				if(hasXsl() && (force == false)) {
					
					StringBuilder sb = new StringBuilder();
					sb.append("href=\"").append(xslPath).append("\" ");
					sb.append("type=\"text/xsl\"");
					
//					document.
					ProcessingInstruction pi = document.createProcessingInstruction(
						"xml-stylesheet",sb.toString());
					Element docel = document.getDocumentElement();
					document.insertBefore(pi,docel);
//					document.insertBefore(document.createTextNode("\n"), docel);

					transformer = tf.newTransformer();
				} else {
					// System.out.println("using " + transformerFactory.getClass().getName() + ".getTransformer(" + xslPath + ")");
					transformer = transformerFactory.getTransformer(xslPath);
					// System.out.println("transformer is " + transformer.getClass().getName());
				}
			} else {
				transformer = tf.newTransformer();
			}
			Source source = new DOMSource(document);


			Result res = new StreamResult(w);
			transformer.transform(source, res);
		} 
		catch (Exception e) {
			System.out.println(sbTrace.toString() + ": " + e.getMessage());
			e.printStackTrace();
			throw new GossamerException(e);
		}
	}

	protected static String getLabel(Object o) {
		if(o == null || RendererUtils.isScalar(o)) {
			return "Item";
		}
		if(o instanceof Map) {
			return "Map";
		}
		if(o instanceof Collection || o.getClass().isArray()) {
			return "List";
		}
		return o.getClass().getSimpleName();
	}

	public String getEncoding()
    {
    	return encoding;
    }

	public void setDateFormat(DateFormat dateFormat)
    {
    	this.dateFormat = dateFormat;
    }

	public String getView()
    {
    	return view;
    }

	public void setView(String view)
    {
    	this.view = view;
    }
	
	public void setDevice(ClientDeviceProfile device) {
		this.device = device;
	}
	public void setForce(boolean force)
    {
    	this.force = force;
    }
	public void setTransformerFactory(XslTransformerFactory transformerFactory)
    {
    	this.transformerFactory = transformerFactory;
    }
}
