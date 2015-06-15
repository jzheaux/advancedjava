package com.joshcummings.java8.streams;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;


public class FileTools {
	public Stream<String> grep(String fileName, String contains) throws IOException {
		int[] lineNumber = { 0 };
		return Files.lines(Paths.get(fileName)).map((line) -> ++lineNumber[0] + ": " + line).filter((line) -> line.contains(contains));
	}
	
	public Stream<String> find(String fileName) throws IOException {
		return Files.walk(Paths.get(".")).filter((path) -> path.toFile().getName().equals(fileName)).map((path) -> path.toAbsolutePath().toString());
	}
	
	
	public static void main(String[] args) throws Exception {
		new FileTools().grep("hs_err_pid23791.log", "Java").forEach(System.out::println);
		new FileTools().find("IdGenerator.java").forEach(System.out::println);
	}
}
