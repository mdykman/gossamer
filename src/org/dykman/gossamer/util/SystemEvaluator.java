package org.dykman.gossamer.util;

public class SystemEvaluator {

		public String getSystemVariable(String key) {
			return System.getenv(key);
		}
}
