package org.dykman.gossamer.webapp.rewrite;

import java.util.regex.Pattern;

class RegexpEntry {
	Pattern pattern;
	int code;
	Emitter[] emitters;
}