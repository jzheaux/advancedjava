package com.joshcummings.java8.strings;

import java.util.StringJoiner;

public class GenericStringJoiner {
	private final StringJoiner sj;
	
	public GenericStringJoiner(String delim, String prefix, String suffix) {
		sj = new StringJoiner(delim, prefix, suffix);
	}
	
	public GenericStringJoiner add(Object obj) {
		sj.add(obj.toString());
		return this;
	}
	
	public GenericStringJoiner merge(GenericStringJoiner other) {
		sj.merge(other.sj);
		return this;
	}
	
	public String toString() {
		return sj.toString();
	}
}
