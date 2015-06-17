package com.joshcummings.networking.serialization;
import java.io.Serializable;


/**
 * @author jzheaux
 *
 */
public class Person implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private final String firstName;
	
	public Person(String firstName) {
		this.firstName = firstName;
	}

	@Override
	public String toString() {
		return "Person [firstName=" + firstName + "]";
	}
}
