package org.dykman.gossamer;

import java.io.OutputStream;

public class System {

	public OutputStream getStdOut() {
		return java.lang.System.out;
	}
	public OutputStream getStdErr() {
		return java.lang.System.err;
	}
}
