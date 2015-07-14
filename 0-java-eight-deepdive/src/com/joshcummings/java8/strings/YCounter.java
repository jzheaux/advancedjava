package com.joshcummings.java8.strings;

public class YCounter {
	public static void main(String[] args) {
		String withYs = "yayayayyyyyyyaya";
		
		long count = withYs
			.chars()
			.filter((ch) -> ch == 'y').count();
		System.out.println(count);
	}
}
