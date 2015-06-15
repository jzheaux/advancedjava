package com.joshcummings.java8.lambda;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class ExceptionConsolidator {	
	public static <T, U, V extends RuntimeException> T consolidate(Callable<T> callable, Function<Throwable,V> f) {
		try {
			return callable.call();
		} catch ( Throwable t ) {
			throw f.apply(t);
		}
	}
	
	public static void main(String[] args) {
		String s = "This string isn't long enough...";
		consolidate(
				() -> s.charAt(12345),
				(t) -> new IllegalArgumentException(t)
		);
	}
}
