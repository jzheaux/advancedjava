package com.joshcummings.java8.streams;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.IntStream;



public class PrimeObsession {
	private boolean isPrime(int n) {
		for ( int div = 2; div <= Math.sqrt(n); div++ ) {
			if ( n % div == 0 ) {
				return false;
			}
		}
		return true;
	}
	
	public IntStream asStream() {
		int[] lastPrime = { 2 };
		return IntStream.iterate(2, i -> {
			for ( int start = lastPrime[0] + 1; ; start++ ) {
				if ( isPrime(start) ) {
					lastPrime[0] = start;
					return lastPrime[0];
				}
			}
		});
	}
	
	public static void main(String[] args)  throws IOException {
		PrimeObsession po = new PrimeObsession();
		po.asStream().limit(10).forEach(System.out::println);
		
		Files.walk(Paths.get("."), 1)
			.filter(p -> !p.toFile().getName().startsWith("."))
			.forEach(System.out::println);
	}
}
