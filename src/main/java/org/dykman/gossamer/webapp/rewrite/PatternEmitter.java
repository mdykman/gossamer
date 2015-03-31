package org.dykman.gossamer.webapp.rewrite;

import java.util.regex.Matcher;

class PatternEmitter implements Emitter {
	int n;
	public PatternEmitter(int n) {
		this.n = n;
	}
	public void emit(Matcher matcher, StringBuffer buffer) {
		buffer.append(matcher.group(n));
	}
}
