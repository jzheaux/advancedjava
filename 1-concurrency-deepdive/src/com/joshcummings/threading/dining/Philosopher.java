package com.joshcummings.threading.dining;

public class Philosopher implements Runnable {
	private int bites;
	
	private final Chopstick first;
	private final Chopstick second;
	
	public Philosopher(Chopstick first, Chopstick second) {
		this.first = first;
		this.second = second;
	}
	
	public void run() {
		long start = System.currentTimeMillis();
		while ( start + 5000 > System.currentTimeMillis() ) {
			// we need to change here to make the philosopher acquire both chopsticks
			// first before proceeding
			bites++;
		}
	}
	
	public int getBites() {
		return bites;
	}
}
