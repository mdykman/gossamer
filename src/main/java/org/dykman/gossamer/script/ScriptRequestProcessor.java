package org.dykman.gossamer.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dykman.gossamer.core.GossamerException;
import org.dykman.gossamer.core.PageController;
import org.dykman.gossamer.core.ResponseBean;
import org.dykman.gossamer.core.SimpleRequestProcessor;
import org.dykman.gossamer.webapp.RequestInfo;

public class ScriptRequestProcessor extends SimpleRequestProcessor {
	protected File scriptBase;
	protected String defaultScript = "default";
	protected String scriptExtensions[];

	public Object handle(String component, String handler,
			Map<String, String> params) {
		return processRequest(component + "/" + handler, params);
	}

	protected String[] directoryExtensions(File dir) {
		String[] res = null;
		File lf = new File(dir, ".extensions");

		BufferedReader br = null;
		try {
			if (lf.exists()) {
				br = new BufferedReader(new FileReader(lf));
				String line;
				List<String> ll = new ArrayList<String>();
				while ((line = br.readLine()) != null) {
					String ext = line.trim();
					if (ext.length() > 0) {
						ll.add(ext);
					}
				}
				br.close();
				res = ll.toArray(new String[ll.size()]);
			}
		} catch (IOException e) {
			// TODO:: put this into the scipt logs
			System.out.println("error reading extensions: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (java.io.IOException e) {
				System.err.println("error closing extensions file");
				e.printStackTrace();
			}
		}
		if (scriptExtensions == null)
			scriptExtensions = res;
		return res != null ? res : scriptExtensions;
	}

	protected File seekScriptFile(File directory, String name, String[] exts) {
		StringBuilder sb;
		File f = new File(directory, name);
		if (exts != null) {
			for (String ext : exts) {
				sb = new StringBuilder();
				sb.append(name).append('.').append(ext);
				f = new File(directory, sb.toString());
				// System.out.println("testing for " + f.getAbsolutePath());
				if (f.exists()) {
					// System.out.println(f.getAbsolutePath()+ " FOUND!!");
					return f;
				}
			}
		} else if (f.isDirectory()) {
			return seekScriptFile(f, defaultScript, directoryExtensions(f));
		}
		return null;
	}

	protected File findScriptFile(File directory, String requestPath) {
		// System.out.println("script directory: " + directory.getAbsolutePath()
		// + ", request path=" + requestPath);
		if (!directory.exists()) {
			return null;
		}
		// System.out.println("directory DOES exist");
		int ind = requestPath.indexOf('/');
		String name;
		if (ind == -1)
			name = requestPath;
		else
			name = requestPath.substring(0, ind);

		String[] exts = directoryExtensions(directory);

		File result = seekScriptFile(directory, name, exts);
		if (result != null) {
			RequestInfo ri = (RequestInfo) applicationContext
					.getBean("requestInfo");
			ri.setPathInfo(ind == -1 ? "" : requestPath.substring(ind));
		} else {
			ind = requestPath.indexOf('/');
			if (ind != -1) {
				directory = new File(directory, requestPath.substring(0, ind));
				result = findScriptFile(directory,
						requestPath.substring(ind + 1));
			}
		}
		return result;
	}

	// the DWR access point
	@Override
	public void processRequest(PageController pg, String requestPath,
			String anon, Map<String, String> params) {
		File scriptFile = findScriptFile(scriptBase, requestPath);

		if (scriptFile != null) {
			String fn = scriptFile.getName();
			int n = fn.indexOf('.');

			runScriptHandler(scriptFile, fn.substring(n + 1), pg,
					fn.substring(0, n), anon, params);
		} else {
			super.processRequest(pg, requestPath, anon, params);
		}
	}

	protected void runScriptHandler(File scriptFile, String scriptType,
			PageController pg, String mtd, String anon,
			Map<String, String> params) {
		ScriptHandler handler = (ScriptHandler) handlerFactory
				.createHandler("script");
		handler.setScriptType(scriptType);
		handler.setScriptName(scriptFile.getPath());

		handler.bind("page", pg);
		try {
			Object result = handler.execute(params);

			if (result != null) {
				ResponseBean rb;
				if (anon != null) {
					rb = new ResponseBean(anon, null, result);
				} else {
					rb = new ResponseBean(scriptType, mtd, result);
				}
				pg.enqueue(rb);
			}
		} catch (Exception e) {
			Throwable c = e;

			do {
				if (c instanceof GossamerException)
					throw (GossamerException) c;
			} while ((c = c.getCause()) != null);

			throw new GossamerException(e);
		}
	}

	public void setScriptBase(File scriptBase) {
		this.scriptBase = scriptBase;
	}
}
