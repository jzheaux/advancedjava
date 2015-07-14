package com.joshcummings.jpa.university;

import java.util.Set;

public interface ReadOnlyCourse {

	public abstract Long getId();

	public abstract String getNumber();

	public abstract String getTitle();

	/**
	 * Use a Set instead of a List because records are unique in databases
	 * @return
	 */
	public abstract Set<ReadOnlyStudent> getStudents();

}