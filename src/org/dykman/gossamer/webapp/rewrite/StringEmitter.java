package org.dykman.gossamer.webapp.rewrite;

import java.util.regex.Matcher;

public 	class StringEmitter implements Emitter {
	String s;
	public StringEmitter(String s) {
		this.s = s;
	}
	public void emit(Matcher matcher, StringBuffer buffer) {
		buffer.append(s);
	}
}