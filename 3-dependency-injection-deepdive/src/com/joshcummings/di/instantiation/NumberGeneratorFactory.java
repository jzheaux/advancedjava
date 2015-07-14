package com.joshcummings.di.instantiation;

import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class NumberGeneratorFactory {
	public NumberGenerator secureRandomGenerator() {
		return new RandomNumberGenerator();
	}
	
	public NumberGenerator randomGenerator() {
		return new RandomNumberGenerator(new Random());
	}
	
	public NumberGenerator primeNumberGenerator() {
		List<Integer> primes = Arrays.asList(7, 17);
		return PrimeNumberGenerator.withPrimes(primes);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
