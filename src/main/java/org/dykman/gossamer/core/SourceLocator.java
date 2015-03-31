package org.dykman.gossamer.core;

import java.io.File;

public interface SourceLocator {
	public String formOutputName(File source);
	public File locateSource(String source);
	public String guessSource(String s);
	public String toWebPath(File f);
}
