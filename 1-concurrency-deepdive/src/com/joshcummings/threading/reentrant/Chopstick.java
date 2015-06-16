package com.joshcummings.threading.reentrant;

public class Chopstick {
	private final int which;
	
	public Chopstick(int which) {
		this.which = which;
	}
	
	public int getWhich() {
		return which;
	}
}
