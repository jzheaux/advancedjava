package com.joshcummings.badpublishing;

public class Escape {
	public Escape() {		
		this.value = "bob";
		new Thread(() -> System.out.println(Escape.this.value)).start();
	}
	
	private String value;
	
	public static void main(String[] args) {
		new Escape();
	}
}
