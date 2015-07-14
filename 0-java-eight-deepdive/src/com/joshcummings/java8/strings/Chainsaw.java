package com.joshcummings.java8.strings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Chainsaw {
	public static void main(String[] args) throws IOException {
		try ( BufferedReader reader =
				new BufferedReader(
				new FileReader(
				new File("log")));
			  Stream<String> lines = reader.lines() ) {
			
			List<String> correlatedLines = 
					lines
						.filter((line) -> line.contains("lcid: 12bda8cf903"))
						.collect(Collectors.toList());
			
			System.out.println(correlatedLines);
		}
	}
}
