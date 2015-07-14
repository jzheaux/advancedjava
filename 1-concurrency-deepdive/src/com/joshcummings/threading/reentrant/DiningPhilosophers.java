package com.joshcummings.threading.reentrant;

import java.util.ArrayList;
import java.util.List;

public class DiningPhilosophers {
	private Table table;
	private List<Philosopher> philosophers = new ArrayList<>();
	
	public DiningPhilosophers(int n) {
		table = new Table(n);
		
		for ( int i = 0; i < n; i++ ) {
			int right = i;
			int left = (i+1) % n;
			if ( i % 2 == 0 ) {
				philosophers.add(new Philosopher(right, left, table)); // how to construct?
			} else {
				philosophers.add(new Philosopher(left, right, table)); // how to construct?
			}
		}
	}
	
	public void go() {
		// loop through the philosophers and start them
		List<Thread> threadsToJoinOn = new ArrayList<>();
		for ( Philosopher p : philosophers ) {
			Thread t = new Thread(p);
			t.start();
			threadsToJoinOn.add(t);
		}
		
		for ( Thread t : threadsToJoinOn ) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		int n = 5;
		DiningPhilosophers dp = new DiningPhilosophers(n);
		dp.go();
		for ( int i = 0; i < n; i++ ) {
			System.out.printf("Philopher #%d ate %d bites\n", i, dp.philosophers.get(i).getBites());
		}
	}
}
