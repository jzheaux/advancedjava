package com.joshcummings.threading.reentrant;

import java.util.concurrent.locks.ReentrantLock;

public class Table {
	// Use reentrant locks to distribute the lock across several method calls
	private ReentrantLock[] locks;
	
	public Table(int n) {
		locks = new ReentrantLock[n];
		for ( int i = 0; i < n; i++ ) {
			locks[i] = new ReentrantLock();
		}
	}
	
	public Chopstick[] pickUpChopsticks(Philosopher philosopher, int first,
			int second) {
		locks[first].lock();
		locks[second].lock();
		
		return new Chopstick[] { new Chopstick(first), new Chopstick(second) };
	}

	public void takeBite(Philosopher philosopher, Chopstick first,
			Chopstick second) {		
		locks[first.getWhich()].lock();
		locks[second.getWhich()].lock();
		
		philosopher.takeBite();
		
		locks[second.getWhich()].unlock();
		locks[first.getWhich()].unlock();
	}

	public void dropChopsticks(Philosopher philosopher, int first, int second) {
		locks[second].unlock();
		locks[first].unlock();
	}
	
}
