package com.joshcummings.threading.dining;

import java.util.ArrayList;
import java.util.List;

public class DiningPhilosophers {
	private List<Philosopher> philosophers = new ArrayList<>();
	
	public DiningPhilosophers(int n) {
		List<Chopstick> chopsticks = new ArrayList<>();
		
		for ( int i = 0; i < n; i++ ) {
			chopsticks.add(new Chopstick(i));
		}
		
		for ( int i = 0; i < n; i++ ) {
			int right = i;
			int left = (i+1) % n;
			philosophers.add(new Philosopher(chopsticks.get(right), chopsticks.get(left))); // how to construct?
			
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
