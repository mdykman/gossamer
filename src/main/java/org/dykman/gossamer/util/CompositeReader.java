package org.dykman.gossamer.util;

import java.io.IOException;
import java.io.Reader;
import java.util.Stack;

public class CompositeReader extends Reader {

	Stack<Reader> readers;
	
	public void add(Reader r) {
		readers.add(r);
	}
	public CompositeReader() {
		readers = new Stack<Reader>();
	}
	public CompositeReader(Reader r) {
		this();
		add(r);
	}
	
	@Override
	public int read(char[] cbuf, int off, int len) 
		throws IOException {
		if(readers.size() == 0) return -1;
		Reader r = readers.peek();
		int n= -1;
		while(n < 0) {
			n = r.read(cbuf, off, len);
			if(n == -1) {
				r.close();
				readers.pop();
				if(readers.size() == 0) {
					return -1;
				} else {
					r = readers.peek();
				}
			}
			
		}
		return n;
	}

	@Override
	public void close() throws IOException {
		IOException ee = null;
		for(Reader r : readers) {
			try {
				r.close();
			} catch(IOException e) {
				if(ee == null) {
					ee = e;
				}
			}
		}
		if(ee != null) {
			throw ee;
		}
	}

}
