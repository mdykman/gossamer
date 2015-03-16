package org.dykman.gossamer.xsl;

import java.io.File;

import org.dykman.gossamer.core.SourceLocator;
import org.dykman.gossamer.device.ClientDeviceProfile;
import org.springframework.context.ApplicationContext;

class SmartXslViewManager extends XslViewManager {
//	private File baseFile;
	protected ApplicationContext applicationContext;

	private ClientDeviceProfile profile;
	private XslResolver xslResolver;
	private StyleResolver styleResolver;

	public SmartXslViewManager(ClientDeviceProfile profile, 
			ApplicationContext applicationContext) {
		this.profile = profile;
		this.applicationContext = applicationContext;
//		this.baseFile = baseFile;
	}

	public void setStyleResolver(StyleResolver styleResolver) {
		this.styleResolver = styleResolver;
	}

	public void setView(String view) {
		super.setView(view);
	}

	public void setStyle(String style) {
		styleResolver.setStyle(style);
	}

	public void setFormat(String format) {
		xslResolver.setFormat(format);
	}
/*
	protected String guessSource(String s) {
		String result = s;
		int n = s.lastIndexOf('.');
		if (n != -1)
			n = s.indexOf('-', n);
		if (n != -1)
			result = s.substring(0, n);
		return result;
	}
*/
	public String getView() {
		if (view == null)
			return null;
		String result = null;

		String xslPath = xslResolver.resolveXslPath(profile, view);

		SourceLocator locator = (SourceLocator) applicationContext
				.getBean("sourceLocator");
		File source = locator.locateSource(locator.guessSource(xslPath));
		if (source == null) {
			source = locator.locateSource(view);
		}

		if (source != null) {
			result = locator.formOutputName(source);
		} else {
			File sv;
			xslPath = xslResolver.resolveXslPath(profile, view + ".xsl");
			sv = locator.locateSource(xslPath);
			if (sv == null) {
				sv = locator.locateSource(view + ".xsl");
			}
			// File sv = new File(baseFile,view + ".xsl");
			if (sv != null && sv.exists())
				result = locator.toWebPath(sv);
			else {
				result = view;
			}
		}
		return result;
	}

	public void setXslResolver(XslResolver xslResolver) {
		this.xslResolver = xslResolver;
	}
}