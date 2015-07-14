package com.joshcummings.di.instantiation;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PrimeNumberGenerator implements NumberGenerator {
	private final Queue<Integer> primeNumbers = new LinkedList<Integer>();
	
	private PrimeNumberGenerator(List<Integer> primes) {
		this.primeNumbers.addAll(primes);
	}
	
	@Override
	public synchronized Integer nextNumber() {
		while ( primeNumbers.isEmpty() ) {
			try {
				wait();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		return primeNumbers.poll();
	}

	public static PrimeNumberGenerator withPrimes(List<Integer> primes) {
		return new PrimeNumberGenerator(primes);
	}
}
