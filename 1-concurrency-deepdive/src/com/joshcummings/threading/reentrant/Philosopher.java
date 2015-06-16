package com.joshcummings.threading.reentrant;

public class Philosopher implements Runnable {
	private int bites;
	
	private final int first;
	private final int second;
	private final Table table;
	
	public Philosopher(int first, int second, Table table) {
		this.first = first;
		this.second = second;
		this.table = table;
	}
	
	public void run() {
		long start = System.currentTimeMillis();
		while ( start + 5000 > System.currentTimeMillis() ) {
			Chopstick[] chopsticks = table.pickUpChopsticks(this, first, second);
			
			table.takeBite(this, chopsticks[0], chopsticks[1]);
			
			table.dropChopsticks(this, second, first);
		}
	}
	
	public int getBites() {
		return bites;
	}

	public void takeBite() {
		bites++;
	}
}
