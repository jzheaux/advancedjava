package com.joshcummings.java8.datetime;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;

public class InstantIsImmutable {
	private final String firstName;
	private final Instant birthDate;
	
	public InstantIsImmutable(String firstName, Instant birthDate) {
		super();
		this.firstName = firstName;
		this.birthDate = birthDate;
	}

	public String getFirstName() {
		return firstName;
	}

	public Instant getBirthDate() {
		return birthDate;
	}
	
	public static void main(String[] args) {
		Instant veryOldPerson = Instant.MIN;
		InstantIsImmutable i = new InstantIsImmutable("John", veryOldPerson);
		Duration d = Duration.between(veryOldPerson, Instant.now());
		System.out.println(d.toDays());
		
		LocalDateTime t = LocalDateTime.now().with(TemporalAdjusters.dayOfWeekInMonth(2, DayOfWeek.MONDAY));
		System.out.println(t);
		
		ZonedDateTime w = t.atZone(ZoneId.of("America/Denver"));
		ZonedDateTime q = w.withZoneSameInstant(ZoneId.of("America/Chicago"));
		System.out.println(w);
		System.out.println(q);
		
		LocalDate date = LocalDate.of(1981, Month.JANUARY, 2);
		Period p = date.until(LocalDate.now());
		System.out.println(p.getYears());
		
		
	}
}
