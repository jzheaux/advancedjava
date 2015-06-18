package com.joshcummings.di.instantiation;

import java.beans.ConstructorProperties;

public class ArithmeticNumberGenerator implements NumberGenerator {
	private final int spacing;
	private final int secondarySpacing;
	
	private volatile int currentNumber;
	
	@ConstructorProperties({ "spacing", "secondarySpacing" })
	public ArithmeticNumberGenerator(int spacing, int secondarySpacing) {
		this.spacing = spacing;
		this.secondarySpacing = secondarySpacing;
	}
	
	public int getSecondarySpacing() {
		return secondarySpacing;
	}
	
	public int getSpacing() {
		return spacing;
	}

	@Override
	public synchronized Integer nextNumber() {
		currentNumber += spacing;
		return currentNumber;
	}
}
