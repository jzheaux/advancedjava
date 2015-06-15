package com.joshcummings.java8.streams;
import java.util.UUID;
import java.util.stream.Stream;


public class IdGenerator {
	public String generateId() {
		return UUID.randomUUID().toString();
	}
	
	public Stream<String> asStream() {
		return Stream.generate(this::generateId);
	}
	
	public static void main(String[] args) {
		IdGenerator g = new IdGenerator();
		g.asStream().limit(10).forEach(System.out::println);
	}
}
