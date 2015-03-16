package org.dykman.gossamer.webapp.rewrite;

import java.util.regex.Matcher;

interface Emitter {
	public void emit(Matcher matcher, StringBuffer buffer);
}