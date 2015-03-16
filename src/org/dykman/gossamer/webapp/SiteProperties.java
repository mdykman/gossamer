package org.dykman.gossamer.webapp;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SiteProperties 
{
	private Map<String,Object> nestedProperties = new HashMap<String,Object>();
	Properties siteProperties = new Properties();
	private Map<String,Object> autoVals = new HashMap<String,Object>();
	
	public void appendSiteProperties(Document doc) {
		Element el = doc.getDocumentElement();
		appendSiteProperties(doc, el, autoVals);
	}

	
	@SuppressWarnings("unchecked")
	public void loadAutoKeys() {
		
		Map<String,Object> map;
		Map<String,Object> result = new HashMap<String,Object>();
		int n = 0;
		
		String auto = siteProperties.getProperty("gossamer.auto","");
		String[] keys = auto.split("[,]");
		
		for(String key : keys) {
			Map<String,Object> mymap = result;
	    	String[] spl = key.split("[.]");
			map = nestedProperties;
			Map<String,Object> rr = result;
			for(String ib : spl) {
				++n;
				Object mm = map.get(ib);
				if(mm == null) {
					break;
				} else if(mm instanceof Map) {
					map = (Map<String,Object>) mm;;
					if(n != spl.length) {
						Map<String,Object> mp = new HashMap<String,Object>();
						mymap.put(ib, mp);
						mymap = mp;
					} else {
						mymap.put(ib, mm);
					}
				} else if(mm instanceof String && n == spl.length) {
					rr.put(ib,mm);
				} else {
					break;
				}
			}
		}
		autoVals = result;
	}

	@SuppressWarnings("unchecked")
	protected void appendSiteProperties(Document doc, Element el,Map<String,Object> props) {
		for(Map.Entry<String,Object> entry : props.entrySet()) {
			Element ne = doc.createElement(entry.getKey());
			el.appendChild(ne);
			Object v = entry.getValue();
			if(v instanceof Map<?,?>) {
				appendSiteProperties(doc, ne, (Map<String,Object>) v);
			} else {
				ne.appendChild(doc.createTextNode(v.toString()));
			}
		}		
	}

	public String get(String key) {
		return siteProperties.getProperty(key);
	}
	
	public Properties getProperties() {
		return new Properties(siteProperties);
	}
	public Map<String,Object> getMap() {
		return nestedProperties;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,Object> getMap(String key) {
		Map<String,Object> map = nestedProperties;
		String[] arr = key.toString().split("[.]");
		for(String k  : arr) {
			Object o = map.get(k);
			if(o != null && o instanceof Map) map = (Map<String,Object>) o; 
			else return null; 
		}
		return map;
	}

	public void setProps(String clpath) 
		throws IOException {
		File f = new File(clpath);

		InputStream is = new FileInputStream(f);
//		InputStream is = getClass().getResourceAsStream(clpath);
		Properties pr = new Properties();
		pr.load(is);
		is.close();
		loadProperties(pr);
		loadAutoKeys();
	}

	@SuppressWarnings("unchecked")
	private  void loadProperties(Properties props)
		throws IOException {
		Map<String,Object> pp = new HashMap<String, Object>();
		for(Map.Entry<?,?> entry : props.entrySet()) {
			Map<String,Object> b = pp;
			String k = entry.getKey().toString();

			String[] spl = k.split("[.]");
			String [] bts = Arrays.copyOf(spl,spl.length -1);
			for(String ib : bts) {
				@SuppressWarnings("rawtypes")
				Map m = (Map)b.get(ib);
				if(m == null) {
					m = new HashMap<Object,Object>();
					b.put(ib, m);
				}
				b = m;
			}
			b.put(spl[spl.length-1], entry.getValue());
		}
		nestedProperties = pp;
		siteProperties = props;
	}
}

