package com.joshcummings.java8.streams;
import java.util.stream.IntStream;


public class FibonacciStream {
	public IntStream asStream() {
		// can't parallelize!
		int[] previous = { 1, 1 };
		return IntStream.iterate(previous[0], i -> {
			int temp = previous[1];
			previous[1] = previous[0] + previous[1];
			previous[0] = temp;
			return previous[0];
		});
	}
	
	public static void main(String[] args) {
		FibonacciStream fs = new FibonacciStream();
		fs.asStream().limit(25).forEach(System.out::println);
	}
}
