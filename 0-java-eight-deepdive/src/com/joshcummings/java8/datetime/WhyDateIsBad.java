package com.joshcummings.java8.datetime;

import java.util.Date;

public class WhyDateIsBad {
	private final String firstName;
	private final String lastName;
	private final Date birthDate;
	
	public WhyDateIsBad(String firstName, String lastName, Date birthDate) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthDate = birthDate;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	@Override
	public String toString() {
		return "WhyDateIsBad [firstName=" + firstName + ", lastName="
				+ lastName + ", birthDate=" + birthDate + "]";
	}
	
	public static void main(String[] args) {
		WhyDateIsBad wdib = new WhyDateIsBad("Josh", "Cummings", new Date(System.currentTimeMillis() - 86400 * 1000));
		
		String firstName = wdib.getFirstName();
		firstName.toLowerCase();
		System.out.println(wdib); // yay, nothing changed!
		
		Date birthDate = wdib.getBirthDate();
		birthDate.setTime(System.currentTimeMillis());
		System.out.println(wdib); // dang, broken encapsulation!
	}
}
