package com.joshcummings.di.instantiation;

import java.util.Random;

public class RandomNumberGenerator implements NumberGenerator {
	private Random random;
	
	@Override
	public Integer nextNumber() {
		return random.nextInt();
	}

}
