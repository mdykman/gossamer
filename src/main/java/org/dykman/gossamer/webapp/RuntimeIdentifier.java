package org.dykman.gossamer.webapp;

import java.util.Date;

public class RuntimeIdentifier
{
	private static final char[] charTable = 
		"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-".toCharArray();
	
	private String identifier;
	
	public RuntimeIdentifier() {
		updateIdentifier();
	}
	public String updateIdentifier() {
		long l = new Date().getTime();
		StringBuilder sb  = new StringBuilder();
		while(l > 0) {
			int m =(int)(l % 64);
			l  /= 64;
			sb.append(charTable[m]);
		}
		return identifier = sb.toString();
	}
	public String getIdentifier() {
		return identifier;
	}
}
