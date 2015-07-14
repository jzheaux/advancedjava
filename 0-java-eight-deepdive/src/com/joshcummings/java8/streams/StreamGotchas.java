package com.joshcummings.java8.streams;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class StreamGotchas {
	/**
	 * Returns 10 values, starting at 5
	 */
	public static void whatsWrong1() {
		IntStream.iterate(0, i -> i + 1)
			.limit(10)
			.skip(5)
			.forEach(System.out::println);
	}
	
	public static void whatsWrong2() {
		IntStream.range(1, 5)
        	.peek(System.out::println)
        	.peek(i -> { 
        		if (i == 5) 
        			throw new RuntimeException("bang");
        	});
	}
	
	/**
	 * Returns current working directory's sub directories and files
	 * 
	 * @throws IOException
	 */
	public static void whatsWrong3() throws IOException {
		Files.walk(Paths.get("."))
	     .filter(p -> !p.toFile().getName().startsWith("."))
	     .forEach(System.out::println);
	}
	
	public static void whatsWrong4() {
		IntStream.iterate(0, i -> ( i + 1 ) % 2)
        .distinct()
        .limit(10)
        .forEach(System.out::println);
	}
	
	public static void whatsWrong5() {
		IntStream.iterate(0, i -> i + 1)
        .forEach(System.out::println);
	}
	
	public static void whatsWrong6() {
		Stream<Integer> s = Stream.of(1, 2, 3, 4, 5);
		s.forEach(System.out::println);
		s.forEach(System.out::println);
	}
	
	public static void main(String[] args) throws Exception {
		whatsWrong5();
	}
}
