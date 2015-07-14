package com.joshcummings.java8.streams;
import java.util.Arrays;


public class TakeTheAveragePrimitive {
	private int[] numbers;
	
	public TakeTheAveragePrimitive(int... numbers) {
		this.numbers = numbers;
	}

	public double java7Average() {
		int sum = 0;
		for ( int n : numbers ){
			sum += n;
		}
		return numbers.length == 0 ? 0 : sum / numbers.length;
	}
	
	public double java8Average() {
		int sum = Arrays.stream(numbers).sum();
		return numbers.length == 0 ? 0 : sum / numbers.length;
	}
}
