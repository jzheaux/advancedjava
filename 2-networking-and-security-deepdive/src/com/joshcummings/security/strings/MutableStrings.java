package com.joshcummings.security.strings;

import java.lang.reflect.Field;

public class MutableStrings {
	private static void toUpperCase(String str) {
		try {
			Field f = String.class.getDeclaredField("value");
			f.setAccessible(true);
			f.set(str, str.toUpperCase().toCharArray());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String... args) {
		final String greeting = "Howdy";
		toUpperCase(greeting);
		System.out.println(greeting);
		System.out.println("Howdy");
	}
}
