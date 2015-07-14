package com.joshcummings.java8.strings;

import java.util.StringJoiner;

public class Strings {
	public static void main(String[] args) {
		StringJoiner object = new StringJoiner(", ", "{", "}");
		StringJoiner property = new StringJoiner(" : ");
		StringJoiner value = new StringJoiner("", "\"", "\"");
		System.out.println(object.
			merge(property.merge(
					value.add("property"))
						.merge(
					value.add("value"))));
		
	}
}
