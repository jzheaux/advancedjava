package com.joshcummings.badpublishing;

public class Holder {
	private int n;
	
	public Holder(int n) {
		this.n = n;
	}
	
	public void assertSanity() {
		if ( n != n ) {
			throw new AssertionError("This statement is false.");
		}
	}
}
