package com.joshcummings.di.instantiation;

import java.security.SecureRandom;
import java.util.Random;

public class RandomNumberGenerator implements NumberGenerator {
	private final Random random;
	
	public RandomNumberGenerator() {
		this.random = new SecureRandom();
	}
	
	public RandomNumberGenerator(Random random) {
		this.random = random;
	}
	
	@Override
	public Integer nextNumber() {
		return random.nextInt();
	}

}
