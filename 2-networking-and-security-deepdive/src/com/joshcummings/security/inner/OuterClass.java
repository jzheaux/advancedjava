package com.joshcummings.security.inner;

public class OuterClass {
	private InnerClassA a = new InnerClassA();
	private String property = "Charles";
	
	public InnerClassA getA() {
		return a;
	}
	
	public class InnerClassA {
		private String value = "Dave";
	}
}
