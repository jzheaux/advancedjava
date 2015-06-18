package com.joshcummings.di.gumballs;

public class Gumball {
	private final Long id;
	
	private final String color;

	public Gumball(Long id, String color) {
		this.id = id;
		this.color = color;
	}

	public Long getId() {
		return id;
	}

	public String getColor() {
		return color;
	}
}
