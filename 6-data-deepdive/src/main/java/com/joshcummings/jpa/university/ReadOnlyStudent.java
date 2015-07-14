package com.joshcummings.jpa.university;

import java.util.Set;

import com.joshcummings.jpa.profile.Profile;

/**
 * Here's a way that we can still communicate the immutability of our object
 * to consuming APIs. Sadly, we don't get the Thread-safety guarantee, but
 * this can help us be more optimistic.
 * 
 * @author jzheaux
 *
 */
public interface ReadOnlyStudent {

	public abstract Long getId();

	public abstract Profile getProfile();

	public abstract Double getGpa();

	public abstract Set<ReadOnlyCourse> getCourses();
}